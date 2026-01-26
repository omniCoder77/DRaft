package com.ethyllium.protocol

import com.draft.proto.RequestVoteProtos
import com.ethyllium.model.Cluster
import com.ethyllium.persistence.FileStorage

class ConsensusModule(
    val nodeId: String,
    val cluster: Cluster,
    val persistence: FileStorage,
    raftState: RaftState,
    private val electionTimer: ElectionTimer
) {
    fun handleRequestVote(request: RequestVoteProtos.RequestVoteRequest): RequestVoteProtos.RequestVoteResponse {
        electionTimer.resetElectionTimer()
        TODO("Not yet implemented")
    }

    fun onElectionTimeout() {
        println("Election timeout! Starting election for node $nodeId")
    }
}