package com.ethyllium.state

import com.ethyllium.protocol.RaftState
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread

class RaftRaftRoleTest {

    @Test
    fun `updateCommitIndex should increase index monotonically`() {
        val state = RaftState()
        state.updateCommitIndex(5)
        assertEquals(5, state.getCommitIndex())


        state.updateCommitIndex(3)
        assertEquals(5, state.getCommitIndex())
    }

    @Test
    fun `awaitNewCommit should block until commit index updates`() {
        val state = RaftState()
        val isFinished = AtomicBoolean(false)
        val latch = CountDownLatch(1)

        thread {
            latch.countDown()
            state.awaitNewCommit()
            isFinished.set(true)
        }

        latch.await()

        Thread.sleep(50)
        assertEquals(false, isFinished.get(), "Thread should be blocked waiting for commit")


        state.updateCommitIndex(1)


        Thread.sleep(100)
        assertEquals(true, isFinished.get(), "Thread should have unblocked")
    }
}