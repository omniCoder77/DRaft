package com.ethyllium.transport.grpc

import com.draft.proto.AppendVoteProtos
import com.draft.proto.RaftServiceGrpcKt
import com.draft.proto.RequestVoteProtos
import com.ethyllium.protocol.ConsensusModule
import io.grpc.ManagedChannel
import io.grpc.Server
import io.grpc.Status
import io.grpc.StatusException
import io.grpc.StatusRuntimeException
import io.grpc.inprocess.InProcessChannelBuilder
import io.grpc.inprocess.InProcessServerBuilder
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class RaftGrpcServiceTest {
    private val consensusModule = mockk<ConsensusModule>()
    private lateinit var server: Server
    private lateinit var channel: ManagedChannel

    private lateinit var stub: RaftServiceGrpcKt.RaftServiceCoroutineStub

    @BeforeEach
    fun setup() {
        val serverName = InProcessServerBuilder.generateName()

        server = InProcessServerBuilder.forName(serverName)
            .directExecutor()
            .addService(RaftGrpcService(consensusModule))
            .build()
            .start()

        channel = InProcessChannelBuilder.forName(serverName)
            .directExecutor()
            .build()

        stub = RaftServiceGrpcKt.RaftServiceCoroutineStub(channel)
    }

    @AfterEach
    fun tearDown() {
        server.shutdownNow()
        channel.shutdownNow()
    }

    @Test
    fun `Field Mapping Integration - verify term and lastLogIndex are not swapped`() = runTest {
        val testTerm = 10L
        val testLastLogIndex = 500L

        val request = RequestVoteProtos.RequestVoteRequest.newBuilder()
            .setTerm(testTerm)
            .setLastLogIndex(testLastLogIndex)
            .setCandidateId("node-1")
            .build()
        val capturedRequest = slot<RequestVoteProtos.RequestVoteRequest>()
        coEvery { consensusModule.handleRequestVote(capture(capturedRequest)) } returns
                RequestVoteProtos.RequestVoteResponse.newBuilder().setVoteGranted(true).build()
        stub.requestVote(request)
        assertEquals(testTerm, capturedRequest.captured.term, "Term was swapped or incorrectly mapped!")
        assertEquals(testLastLogIndex, capturedRequest.captured.lastLogIndex, "LastLogIndex was swapped!")
    }

    @Test
    fun `Network Failure Integration - simulate server crash`() = runTest {
        server.shutdownNow()
        val exception = assertThrows<StatusException> {
            stub.requestVote(RequestVoteProtos.RequestVoteRequest.getDefaultInstance())
        }

        assertEquals(Status.Code.UNAVAILABLE, exception.status.code)
    }

    @Test
    fun `AppendEntries Streaming Integration - verify bi-directional flow`() = runTest {
        val responses = listOf(
            AppendVoteProtos.AppendEntriesResponse.newBuilder().setSuccess(true).build(),
            AppendVoteProtos.AppendEntriesResponse.newBuilder().setSuccess(false).build()
        )

        coEvery { consensusModule.handleAppendEntries(any()) } returns responses[0] andThen responses[1]

        val requests = flowOf(
            AppendVoteProtos.AppendEntriesRequest.newBuilder().setTerm(1).build(),
            AppendVoteProtos.AppendEntriesRequest.newBuilder().setTerm(2).build()
        )

        val results = mutableListOf<AppendVoteProtos.AppendEntriesResponse>()
        stub.appendEntries(requests).toList(results)

        assertEquals(2, results.size)
        assertTrue(results[0].success)
        assertFalse(results[1].success)
    }

}