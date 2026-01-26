package com.ethyllium.transport.grpc

import com.draft.proto.AppendVoteProtos
import com.draft.proto.RaftServiceGrpcKt
import com.draft.proto.RequestVoteProtos
import com.ethyllium.protocol.ConsensusModule
import kotlinx.coroutines.flow.Flow

class RaftGrpcService(private val consensusModule: ConsensusModule) : RaftServiceGrpcKt.RaftServiceCoroutineImplBase() {

    override suspend fun requestVote(request: RequestVoteProtos.RequestVoteRequest): RequestVoteProtos.RequestVoteResponse {
        return consensusModule.handleRequestVote(request)
    }

    override fun appendEntries(requests: Flow<AppendVoteProtos.AppendEntriesRequest>): Flow<AppendVoteProtos.AppendEntriesResponse> {
        return super.appendEntries(requests)
    }
}