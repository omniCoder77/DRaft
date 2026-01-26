package com.ethyllium.protocol

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class RaftStateTest {

    @Test
    fun `should ignore decreasing commit index updates`() {
        val state = RaftState()
        state.updateCommitIndex(10)
        assertEquals(10, state.getCommitIndex())

        state.updateCommitIndex(5)
        assertEquals(10, state.getCommitIndex(), "Commit index should not decrease")
    }

    @Test
    fun `should ignore decreasing lastApplied updates`() {
        val state = RaftState()
        state.updateLastApplied(8)
        assertEquals(8, state.getLastApplied())

        state.updateLastApplied(3)
        assertEquals(8, state.getLastApplied(), "Last applied index should not decrease")
    }

    @Test
    fun `awaitNewCommit should unblock on commit index update`() {
        val state = RaftState()
        var unblocked = false

        val thread = Thread {
            state.awaitNewCommit()
            unblocked = true
        }
        thread.start()

        Thread.sleep(100)
        assertFalse(unblocked, "Thread should be blocked waiting for commit")

        state.updateCommitIndex(1)

        thread.join(1000)
        assertTrue(unblocked, "Thread should have unblocked after commit index update")
    }

    @Test
    fun `awaitNewCommit must return immediately when commitIndex is greater than lastApplied`() {
        val state = RaftState()
        state.updateCommitIndex(5)
        state.updateLastApplied(3)

        val startTime = System.currentTimeMillis()
        state.awaitNewCommit()
        val duration = System.currentTimeMillis() - startTime

        assertTrue(duration < 100, "awaitNewCommit should return immediately when commitIndex > lastApplied")
    }

    @Test
    fun `should wake up all waiting threads when index is updated`() {
        val totalThreads = 3
        val latch = CountDownLatch(totalThreads)
        val service = RaftState()
        repeat(totalThreads) {
            thread {
                try {
                    service.awaitNewCommit()
                    latch.countDown()
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                }
            }
        }
        Thread.sleep(100)
        service.updateCommitIndex(10)
        val allWokeUp = latch.await(500, TimeUnit.MILLISECONDS)

        assertTrue(allWokeUp, "Not all threads woke up! Remaining count: ${latch.count}")
    }
}