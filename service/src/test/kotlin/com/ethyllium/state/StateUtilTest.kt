package com.ethyllium.state

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.io.File

class StateUtilTest {

    private val util = StateUtil()
    private val stateFile = File("machine_state")
    private val tempFile = File("machine_state.tmp")

    @AfterEach
    fun tearDown() {
        if (stateFile.exists()) stateFile.delete()
        if (tempFile.exists()) tempFile.delete()
    }

    @Test
    fun `should return null when no state exists`() {
        val result = util.load()
        assertNull(result, "Should be null if file doesn't exist")
    }

    @Test
    fun `should persist and load state correctly`() {
        val term = 5L
        val votedFor = "node-1"
        util.persist(term, votedFor)
        val loaded = util.load()
        assertNotNull(loaded)
        assertEquals(term, loaded!!.first)
        assertEquals(votedFor, loaded.second)
    }

    @Test
    fun `should overwrite existing state`() {
        util.persist(1L, "node-1")
        util.persist(2L, "node-2")

        val loaded = util.load()

        assertEquals(2L, loaded!!.first)
        assertEquals("node-2", loaded.second)
    }
}