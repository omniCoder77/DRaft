package com.ethyllium.protocol

import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.locks.ReentrantLock

class RaftState {

    private val _commitIndex = AtomicLong(0)
    private val _lastApplied = AtomicLong(0)

    private val lock = ReentrantLock()
    private val newCommitAvailable = lock.newCondition()

    fun getCommitIndex(): Long = _commitIndex.get()

    fun getLastApplied(): Long = _lastApplied.get()

    /**
     * Updates commitIndex.
     * Called when we learn logs are replicated to a quorum.
     */
    fun updateCommitIndex(newIndex: Long) {
        lock.lock()
        try {
            val current = _commitIndex.get()
            // Raft safety: commitIndex never decreases
            if (newIndex > current) {
                _commitIndex.set(newIndex)

                // Signal any thread waiting in awaitNewCommit()
                newCommitAvailable.signalAll()
            }
        } finally {
            lock.unlock()
        }
    }

    /**
     * Updates lastApplied.
     * Called after the State Machine has processed the log.
     */
    fun updateLastApplied(newIndex: Long) {
        // No lock needed here typically, as only one thread should be applying logs
        // But we must ensure monotonicity
        val current = _lastApplied.get()
        if (newIndex > current) {
            _lastApplied.set(newIndex)
        }
    }

    /**
     * Blocks the calling thread until commitIndex > lastApplied.
     * This prevents CPU spinning (busy-wait).
     */
    fun awaitNewCommit() {
        lock.lock()
        try {
            // While there is nothing new to apply, we sleep.
            while (_commitIndex.get() <= _lastApplied.get()) {
                // Wait specifically releases the lock and sleeps
                newCommitAvailable.await()
            }
        } finally {
            lock.unlock()
        }
    }
}