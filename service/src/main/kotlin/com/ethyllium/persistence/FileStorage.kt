package com.ethyllium.persistence

import java.io.File
import java.util.Properties
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class FileStorage(
    storageDir: File
) {
    private val file = File(storageDir, "machine_state")
    private val tempFile = File(storageDir, "machine_state.tmp")

    // In-memory cache for fast access
    private var _currentTerm: Long = 0
    private var _votedFor: String? = null

    // Lock to handle concurrent access between RPC threads
    private val lock = ReentrantReadWriteLock()

    init {
        // Load state into memory immediately upon startup
        val state = loadFromDisk()
        if (state != null) {
            _currentTerm = state.first
            _votedFor = state.second
        } else {
            // Initial boot state (Raft spec)
            _currentTerm = 0
            _votedFor = null
            persistToDisk(0, null) // Ensure file exists
        }
    }

    fun getCurrentTerm(): Long = lock.read { _currentTerm }

    fun getVotedFor(): String? = lock.read { _votedFor }

    /**
     * Updates term and votedFor.
     * Guaranteed to flush to disk BEFORE returning, satisfying Raft safety.
     */
    fun updateState(newTerm: Long, newVotedFor: String?) {
        lock.write {
            // Don't write to disk if nothing changed
            if (_currentTerm == newTerm && _votedFor == newVotedFor) return

            // 1. Write to Stable Storage first (Critical Raft Rule)
            persistToDisk(newTerm, newVotedFor)

            _currentTerm = newTerm
            _votedFor = newVotedFor
        }
    }


    private fun persistToDisk(term: Long, voted: String?) {
        val props = Properties()
        props.setProperty("currentTerm", term.toString())
        if (voted != null) {
            props.setProperty("votedFor", voted)
        }

        tempFile.outputStream().use { fos ->
            props.store(fos, "Raft Persistent State")
            // fsync: Force operating system to write to physical disk
            fos.channel.force(true)
        }

        // Atomic rename ensures we never have a corrupted half-written file
        if (!tempFile.renameTo(file)) {
            // On Windows, renameTo might fail if target exists, delete first
            if (System.getProperty("os.name").lowercase().contains("win")) {
                file.delete()
                if (!tempFile.renameTo(file)) throw IllegalStateException("Failed to rename file")
            } else {
                throw IllegalStateException("Failed to persist Raft state via rename")
            }
        }
    }

    private fun loadFromDisk(): Pair<Long, String?>? {
        if (!file.exists()) return null

        val props = Properties()
        file.inputStream().use { props.load(it) }

        val term = props.getProperty("currentTerm", "0").toLong()
        // votedFor can be null in Raft
        val voted = props.getProperty("votedFor")

        return Pair(term, voted)
    }
}