package com.ethyllium.protocol

import com.draft.proto.AppendVoteProtos
import com.draft.proto.RequestVoteProtos
import com.ethyllium.model.Cluster
import com.ethyllium.persistence.FileStorage

class ConsensusModule(
    val cluster: Cluster,
    val persistence: FileStorage,
    val raftState: RaftState,
    private val electionTimer: ElectionTimer
) {
    fun handleRequestVote(request: RequestVoteProtos.RequestVoteRequest): RequestVoteProtos.RequestVoteResponse {
        electionTimer.resetElectionTimer()
        return RequestVoteProtos.RequestVoteResponse.newBuilder().build()
    }

    fun onElectionTimeout() {
        println("Election timeout! Starting election for node ${cluster.myselfId}")
    }

    fun handleAppendEntries(request: AppendVoteProtos.AppendEntriesRequest): AppendVoteProtos.AppendEntriesResponse {
        return AppendVoteProtos.AppendEntriesResponse.newBuilder().build()
    }
}