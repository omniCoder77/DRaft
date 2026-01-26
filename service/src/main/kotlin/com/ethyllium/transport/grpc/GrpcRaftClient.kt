package com.ethyllium.transport.grpc

import com.draft.proto.RaftServiceGrpcKt
import com.draft.proto.RequestVoteProtos
import com.ethyllium.transport.RaftClient
import com.ethyllium.model.Node
import io.grpc.ManagedChannelBuilder

class GrpcRaftClient(node: Node) : RaftClient {

    private val channel = ManagedChannelBuilder.forAddress(node.host, node.port).usePlaintext().build()
    private val stub = RaftServiceGrpcKt.RaftServiceCoroutineStub(channel)

   override suspend fun sendRequestVote(
        term: Long,
        candidateId: String,
        lastLogIndex: Long,
        lastLogTerm: Long
    ) {
        val request = RequestVoteProtos.RequestVoteRequest
            .newBuilder()
            .setTerm(term)
            .setCandidateId(candidateId)
            .setLastLogIndex(lastLogIndex)
            .setLastLogTerm(term)
            .build()
        val response = stub.requestVote(request)
    }

    override suspend fun sendAppendEntries() {
        TODO("Not yet implemented")
    }
}