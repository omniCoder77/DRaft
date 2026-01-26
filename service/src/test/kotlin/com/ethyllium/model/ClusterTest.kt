package com.ethyllium.model

import com.ethyllium.transport.RaftClient
import com.ethyllium.transport.grpc.GrpcRaftClient
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ClusterTest {

    @Test
    fun `should calculate quorum correctly`() {
        val nodes = mapOf(
            "node-2" to mockk<RaftClient>(),
            "node-3" to mockk<RaftClient>()
        )

        val cluster = Cluster(nodes, "node-1")

        Assertions.assertEquals(3, cluster.size)
        Assertions.assertEquals(2, cluster.quorumSize())
    }

    @Test
    fun `should calculate quorum for even number of nodes`() {
        val nodes = mapOf(
            "node-2" to mockk<RaftClient>(),
            "node-3" to mockk<RaftClient>(),
            "node-4" to mockk<RaftClient>()
        )

        val cluster = Cluster(nodes, "node-1")

        Assertions.assertEquals(4, cluster.size)
        Assertions.assertEquals(3, cluster.quorumSize())
    }

    @Test
    fun `quorum size should be 1 for single node cluster`() {
        val cluster = Cluster(emptyMap(), "node-1")

        Assertions.assertEquals(1, cluster.size)
        Assertions.assertEquals(1, cluster.quorumSize())
    }

    @Test
    fun `quorum size should be 0 for single node cluster`() {
        val cluster = Cluster(emptyMap(), "node-1")

        Assertions.assertEquals(1, cluster.size)
        Assertions.assertEquals(1, cluster.quorumSize())
    }

    @Test
    fun `verify node`() {
        val maxNodes = 50
        val nodes = (1..maxNodes).associate { i ->
            "node-$i" to GrpcRaftClient(Node("localhost", 5000 + i))
        }
        val cluster = Cluster(nodes, "node")
        for (i in 1..maxNodes) {
            Assertions.assertEquals(nodes["node-$i"], cluster.nodes["node-$i"])
        }
    }

    @Test
    fun `should fail on self inclusion in map`() {
        val maxNodes = 50
        val nodes = (1..maxNodes).associate { i ->
            "node-$i" to GrpcRaftClient(Node("localhost", 5000 + i))
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            Cluster(nodes,"node-1")
        }
    }


}