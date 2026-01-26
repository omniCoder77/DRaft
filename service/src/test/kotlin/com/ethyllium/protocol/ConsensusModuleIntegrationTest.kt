package com.ethyllium.protocol

import com.ethyllium.model.Cluster
import com.ethyllium.model.Node
import com.ethyllium.persistence.FileStorage
import com.ethyllium.transport.grpc.GrpcRaftClient
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.io.path.createTempDirectory

class ConsensusModuleIntegrationTest {

    private lateinit var consensusModule: ConsensusModule

    @BeforeEach
    fun setup() {
        val nodes = (1..5).associate {
            "node-$it" to GrpcRaftClient(Node(host = "localhost", port = 5000 + it))
        }
        val cluster = Cluster(nodes, "node")
        val fileStorage = FileStorage(createTempDirectory().toFile())
        val raftState = RaftState()
        val electionTimer = ElectionTimer()
        consensusModule = ConsensusModule(
            cluster,
            fileStorage,
            raftState,
            electionTimer
        )
    }

    @Test
    fun `initial currentTerm should be zero`() {
        val currentTerm = consensusModule.persistence.getCurrentTerm()
        Assertions.assertEquals(0L, currentTerm, "New cluster nodes must start at term 0")
    }

    @Test
    fun `initial votedFor should be null`() {
        val votedFor = consensusModule.persistence.getVotedFor()
        Assertions.assertNull(votedFor, "New cluster nodes should not have voted for anyone")
    }

    @Test
    fun `initial lastApplied should be zero`() {
        val lastApplied = consensusModule.raftState.getLastApplied()
        Assertions.assertEquals(0, lastApplied, "State machine should start with 0 applied logs")
    }

    @Test
    fun `initial commitIndex should match lastApplied`() {
        val lastApplied = consensusModule.raftState.getLastApplied()
        val lastCommitIndex = consensusModule.raftState.getCommitIndex()
        Assertions.assertEquals(
            lastApplied,
            lastCommitIndex,
            "Commit index must stay in sync with last applied on startup"
        )
    }
}