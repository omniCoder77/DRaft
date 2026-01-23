package com.ethyllium.state

import java.io.File
import java.util.Properties

class StateUtil {

    private val file = File("machine_state")
    private val tempFile = File("machine_state.tmp")

    /**
     * Persists the current term and votedFor to disk.
     * @param currentTerm The current term to persist.
     * @param votedFor The candidate ID that received vote in current term.
     */
    fun persist(currentTerm: Long, votedFor: String) {
        val props = Properties()
        props.setProperty("currentTerm", currentTerm.toString())
        props.setProperty("votedFor", votedFor)

        tempFile.outputStream().use { fos ->
            props.store(fos, "Raft Persistent State")
            fos.channel.force(true)
        }

        if (!tempFile.renameTo(file)) {
            throw IllegalStateException("Failed to persist Raft state via rename")
        }
    }

    /**
     * Loads the persisted state from disk.
     * @return Pair of currentTerm and votedFor, or null if no state is persisted found.
     */
    fun load(): Pair<Long, String>? {
        if (!file.exists()) return null

        val props = Properties()
        file.inputStream().use { props.load(it) }

        return Pair(
            props.getProperty("currentTerm").toLong(),
            props.getProperty("votedFor")
        )
    }
}