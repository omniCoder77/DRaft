package com.ethyllium.state

import com.ethyllium.persistence.FileStorage
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File

class PersistentRaftRoleTest {

    private val testDir = File("test_raft_data")

    @AfterEach
    fun tearDown() {
        testDir.deleteRecursively()
    }

    @Test
    fun `should initialize with term 0 if no state exists`() {
        testDir.mkdirs()
        val state = FileStorage(testDir)

        assertEquals(0L, state.getCurrentTerm())
        assertNull(state.getVotedFor())
    }

    @Test
    fun `should persist state and load it on reboot`() {
        testDir.mkdirs()

        val state1 = FileStorage(testDir)
        state1.updateState(5L, "node-2")

        assertEquals(5L, state1.getCurrentTerm())
        assertEquals("node-2", state1.getVotedFor())

        val state2 = FileStorage(testDir)

        assertEquals(5L, state2.getCurrentTerm())
        assertEquals("node-2", state2.getVotedFor())
    }

    @Test
    fun `should update term and clear vote`() {
        testDir.mkdirs()
        val state = FileStorage(testDir)

        state.updateState(1L, "node-1")
        assertEquals("node-1", state.getVotedFor())

        state.updateState(2L, null)

        assertEquals(2L, state.getCurrentTerm())
        assertNull(state.getVotedFor())

        val rebooted = FileStorage(testDir)
        assertEquals(2L, rebooted.getCurrentTerm())
        assertNull(rebooted.getVotedFor())
    }

    @Test
    fun `should handle concurrent updates safely`() {
        testDir.mkdirs()
        val state = FileStorage(testDir)
        val threads = 10
        val latch = java.util.concurrent.CountDownLatch(threads)
        val errors = java.util.concurrent.atomic.AtomicInteger(0)

        for (i in 1..threads) {
            kotlin.concurrent.thread {
                try {
                    state.updateState(i.toLong(), "node-$i")
                } catch (e: Exception) {
                    errors.incrementAndGet()
                    e.printStackTrace()
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await()
        assertEquals(0, errors.get(), "Concurrency errors occurred")

        val reloaded = FileStorage(testDir)
        assertEquals(state.getCurrentTerm(), reloaded.getCurrentTerm())
        assertEquals(state.getVotedFor(), reloaded.getVotedFor())
    }
}