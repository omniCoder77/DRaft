package com.ethyllium.state

import java.util.concurrent.atomic.AtomicLong

class VolatileState {
    private val commitIndex: AtomicLong = AtomicLong(0)
    private val lastApplied: AtomicLong = AtomicLong(0)

    override fun toString(): String {
        return "VolatileState(commitIndex=${commitIndex.get()}, lastApplied=${lastApplied.get()})"
    }

    fun getCommitIndex(): Long {
        return commitIndex.get()
    }

    fun setCommitIndex(value: Long) {
        commitIndex.set(value)
    }

    fun getLastApplied(): Long {
        return lastApplied.get()
    }

    fun setLastApplied(value: Long) {
        lastApplied.set(value)
    }
}