package com.ethyllium.persistence

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.io.path.createTempDirectory

class FileStorageTest {

    private val testDir = createTempDirectory().toFile()
    private val storage = FileStorage(testDir)

    @BeforeEach
    fun setUp() {
        testDir.deleteRecursively()
        testDir.mkdirs()
    }

    @Test
    fun `should initialize currentTerm with 0 and votedFor with null on fresh install`() {
        assertEquals(0L, storage.getCurrentTerm())
        assertNull(storage.getVotedFor())
    }

    @Test
    fun `should update term correctly`() {
        val term = storage.getCurrentTerm()
        val votedFor = storage.getVotedFor()
        storage.updateState(term + 1, votedFor)
        assertEquals(term + 1, storage.getCurrentTerm())
    }

    @Test
    fun `should update votedFor correctly`() {
        val term = storage.getCurrentTerm()
        val votedFor = "node-1"
        storage.updateState(term, votedFor)
        assertEquals(votedFor, storage.getVotedFor())
    }

    @Test
    fun `should update both term and votedFor correctly`() {
        val newTerm = 5L
        val newVotedFor = "node-2"
        storage.updateState(newTerm, newVotedFor)
        assertEquals(newTerm, storage.getCurrentTerm())
        assertEquals(newVotedFor, storage.getVotedFor())
    }

    @Test
    fun `should not update state if values are the same`() {
        val initialTerm = storage.getCurrentTerm()
        val initialVotedFor = storage.getVotedFor()
        storage.updateState(initialTerm, initialVotedFor)
        assertEquals(initialTerm, storage.getCurrentTerm())
        assertEquals(initialVotedFor, storage.getVotedFor())
    }

    @Test
    fun `should handle corrupted properties file`() {
        val dir = File(testDir, "corrupt_test_dir")
        dir.mkdirs()
        val targetFile = File(dir, "raft_metadata.properties")
        targetFile.writeText("!!! THIS IS CORRUPTED DATA !!!")
        val storage = FileStorage(dir)
        assertEquals(0L, storage.getCurrentTerm())
        assertNull(storage.getVotedFor())
    }

    @Test
    fun `should survive reboot and load persisted state`() {
        val newTerm = 10L
        val newVotedFor = "node-3"
        storage.updateState(newTerm, newVotedFor)
        val reloadedStorage = FileStorage(testDir)
        assertEquals(newTerm, reloadedStorage.getCurrentTerm())
        assertEquals(newVotedFor, reloadedStorage.getVotedFor())
    }
}