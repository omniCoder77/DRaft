package com.ethyllium.protocol

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class ElectionTimer(
    private val electionTimeoutUpperBound: Long = 300,
    private val electionTimeoutLowerBound: Long = 150,
) {

    private val scheduler = Executors.newSingleThreadScheduledExecutor { runnable ->
        Thread(runnable, "election-timer-thread")
    }
    private var electionTask: ScheduledFuture<*>? = null
    private var onElectionTimeout: (() -> Unit)? = null

    /**
     * Sets the callback to be invoked when the election timer expires.
     * @param callback The lambda function to call on election timeout.
     */
    fun setElectionCallback(callback: () -> Unit) {
        this.onElectionTimeout = callback
    }

    /**
     * Resets the election timer to prevent starting a new election.
     *
     * Cancels any pending election task without interrupting it if already executing
     * (see [ScheduledFuture.cancel]). This ensures that if an election has just begun,
     * it completes rather than being interrupted mid-execution, which could leave node
     * state inconsistent.
     *
     * - A valid AppendEntries (heartbeat) is received from the current leader (see [com.ethyllium.transport.grpc.RaftGrpcService.appendEntries])
     * - A vote is granted to a candidate
     * - Transitioning to follower state
     */
    fun resetElectionTimer() {
        // cancel any pending election task
        electionTask?.cancel(false)
        // randomized election timeout after every heartbeat
        val timeout = Random.nextLong(electionTimeoutLowerBound, electionTimeoutUpperBound)
        electionTask = scheduler.schedule(
            {
                startElection()
                onElectionTimeout?.invoke()
            },
            timeout,
            TimeUnit.MILLISECONDS
        )
    }

    fun startElection() {}

    fun shutdown() {
        scheduler.shutdown()
    }
}