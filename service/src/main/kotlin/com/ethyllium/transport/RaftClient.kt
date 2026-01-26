package com.ethyllium.transport

interface RaftClient {
    suspend fun sendAppendEntries()
    suspend fun sendRequestVote(
        term: Long,
        candidateId: String,
        lastLogIndex: Long,
        lastLogTerm: Long
    )
}