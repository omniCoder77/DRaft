package com.ethyllium.protocol

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class ElectionTimerTest {

    private var electionTimer = ElectionTimer()

    @BeforeEach
    fun setup() {
        electionTimer = ElectionTimer(
            electionTimeoutLowerBound = 50,
            electionTimeoutUpperBound = 100
        )
    }

    @AfterEach
    fun tearDown() {
        electionTimer.shutdown()
    }

    @Test
    fun `should trigger callback within bounds`() {
        val latch = CountDownLatch(1)
        val start = System.currentTimeMillis()
        electionTimer.setOnElectionTimeoutCallback {
            latch.countDown()
        }
        electionTimer.resetElectionTimer()

        val success = latch.await(200, TimeUnit.MILLISECONDS)
        val duration = System.currentTimeMillis() - start

        Assertions.assertTrue(success, "Election callback never fired")
        Assertions.assertTrue(duration >= 50, "Fired too early: $duration ms")
    }

    @Test
    fun `reset should cancel the previous task`() {
        // used larger time windows so os jitter don't break logic
        val timer = ElectionTimer(400, 200)
        val counter = AtomicInteger(0)
        timer.setOnElectionTimeoutCallback {
            counter.incrementAndGet()
        }
        timer.resetElectionTimer()

        Thread.sleep(100)
        timer.resetElectionTimer()
        Thread.sleep(150)
        Assertions.assertEquals(0, counter.get(), "Timer fired despite being reset")
        timer.shutdown()
    }

    @Test
    fun `should reset timer`() {
        val counter = AtomicInteger(0)
        electionTimer.setOnElectionTimeoutCallback { counter.incrementAndGet() }
        electionTimer.resetElectionTimer()
        Thread.sleep(30)
        electionTimer.resetElectionTimer()
        Thread.sleep(30)
        electionTimer.resetElectionTimer()
        Assertions.assertEquals(0, counter.get(), "Timer did not reset correctly")
    }

    @Test
    fun `should fire callback when timer expires`() {
        val counter = AtomicInteger(0)
        electionTimer.setOnElectionTimeoutCallback { counter.incrementAndGet() }
        electionTimer.resetElectionTimer()
        Thread.sleep(150)
        Assertions.assertEquals(1, counter.get(), "Timer did not fire correctly")
    }

    @Test
    fun `should stop when shutdown is called`() {
        val counter = AtomicInteger(0)
        electionTimer.setOnElectionTimeoutCallback { counter.incrementAndGet() }
        electionTimer.resetElectionTimer()
        electionTimer.shutdown()
        Thread.sleep(150)
        Assertions.assertEquals(0, counter.get(), "Timer fired after shutdown")
    }

    @Test
    fun `should survive callback exception and remain functional`() {
        val latch = CountDownLatch(2)
        var callCount = 0
        electionTimer.setOnElectionTimeoutCallback {
            callCount++
            latch.countDown()
            if (callCount == 1) {
                throw RuntimeException("Simulated crash")
            }
        }
        electionTimer.resetElectionTimer()
        Thread.sleep(100)
        electionTimer.resetElectionTimer()
        val success = latch.await(500, TimeUnit.MILLISECONDS)
        Assertions.assertTrue(success, "The second callback never fired; the thread might be dead.")
        Assertions.assertEquals(2, callCount, "Expected callback to have run twice.")
    }

    @Test
    fun `throw exception if upper bound is less than lower bound`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            ElectionTimer(electionTimeoutUpperBound = 100, electionTimeoutLowerBound = 200)
        }
    }
}