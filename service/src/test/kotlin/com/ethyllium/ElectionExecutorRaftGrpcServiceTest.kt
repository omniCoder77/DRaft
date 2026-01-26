package com.ethyllium

import com.ethyllium.protocol.ElectionTimer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class ElectionExecutorRaftGrpcServiceTest {

    @Test
    fun `should trigger callback within bounds`() {
        val latch = CountDownLatch(1)
        val service = ElectionTimer(
            electionTimeoutLowerBound = 50,
            electionTimeoutUpperBound = 100
        )

        val start = System.currentTimeMillis()
        service.setElectionCallback {
            latch.countDown()
        }
        service.resetElectionTimer()

        val success = latch.await(200, TimeUnit.MILLISECONDS)
        val duration = System.currentTimeMillis() - start

        assertTrue(success, "Election callback never fired")
        assertTrue(duration >= 50, "Fired too early: $duration ms")
        service.shutdown()
    }

    @Test
    fun `resetElectionTimer should prevent previous task from firing`() {
        val counter = java.util.concurrent.atomic.AtomicInteger(0)
        val service = ElectionTimer(
            electionTimeoutLowerBound = 100,
            electionTimeoutUpperBound = 200
        )
        service.setElectionCallback {
            counter.incrementAndGet()
        }
        service.resetElectionTimer()
        Thread.sleep(50)
        service.resetElectionTimer()
        Thread.sleep(60)
        assertEquals(0, counter.get(), "Timer fired despite being reset")

        service.shutdown()
    }
}