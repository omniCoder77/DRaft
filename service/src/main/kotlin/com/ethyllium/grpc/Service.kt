package com.ethyllium.grpc

import com.draft.proto.RaftServiceGrpcKt
import com.draft.proto.RequestVoteProtos

class Service : RaftServiceGrpcKt.RaftServiceCoroutineImplBase() {

    override suspend fun requestVote(request: RequestVoteProtos.RequestVoteRequest): RequestVoteProtos.RequestVoteResponse {
        return super.requestVote(request)
    }
}