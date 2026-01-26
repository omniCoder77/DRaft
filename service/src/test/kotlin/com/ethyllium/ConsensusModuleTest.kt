package com.ethyllium

import com.draft.proto.RequestVoteProtos
import com.ethyllium.model.Cluster
import com.ethyllium.protocol.ConsensusModule
import com.ethyllium.protocol.ElectionTimer
import com.ethyllium.persistence.FileStorage
import com.ethyllium.protocol.RaftState
import io.mockk.mockk
import org.junit.jupiter.api.Test
import java.io.File

class ConsensusModuleTest {

    private val cluster = mockk<Cluster>(relaxed = true)
    private val raftState = RaftState()
    private val electionService = mockk<ElectionTimer>(relaxed = true)

    private val tempDir = File("build/tmp/raft_test").apply { mkdirs() }
    private val persistence = FileStorage(tempDir)

    @Test
    fun `handleRequestVote should reset election timer if vote granted`() {
        ConsensusModule(
            "node-1", cluster, persistence, raftState, electionService
        )

        RequestVoteProtos.RequestVoteRequest.newBuilder()
            .setTerm(2)
            .setCandidateId("node-2")
            .setLastLogIndex(0)
            .setLastLogTerm(0)
            .build()
    }
}