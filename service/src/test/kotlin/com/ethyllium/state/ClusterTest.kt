package com.ethyllium.state

import com.ethyllium.transport.RaftClient
import com.ethyllium.model.Cluster
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ClusterTest {

    @Test
    fun `should calculate quorum correctly`() {
        val nodes = mapOf(
            "node-2" to mockk<RaftClient>(),
            "node-3" to mockk<RaftClient>()
        )

        val cluster = Cluster(nodes, "node-1")

        assertEquals(3, cluster.size)
        assertEquals(2, cluster.quorumSize())
    }

    @Test
    fun `should calculate quorum for even number of nodes`() {
        val nodes = mapOf(
            "node-2" to mockk<RaftClient>(),
            "node-3" to mockk<RaftClient>(),
            "node-4" to mockk<RaftClient>()
        )

        val cluster = Cluster(nodes, "node-1")

        assertEquals(4, cluster.size)
        assertEquals(3, cluster.quorumSize())
    }
}