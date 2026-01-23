package com.ethyllium.state

data class MachineState(
    val state: State,
    val currentTerm: Int,
    val votedFor: String? = null,
    val logs: List<Log> = emptyList() // empty list indicating a heartbeat
)