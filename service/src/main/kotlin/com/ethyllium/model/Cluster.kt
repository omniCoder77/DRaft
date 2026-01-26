package com.ethyllium.model

import com.ethyllium.transport.RaftClient

/**
 * Represents a collection of machines in a distributed system. This is static and does not change during runtime.
 * No need to store host and port of current machine, as default host will be localhost and port is 8080 (see [com.ethyllium.main]).
 * @property nodes Map of key as nodeId and value as corresponding [Node].
 * @property myselfId The id of the current node.
 */
data class Cluster(val nodes: Map<String, RaftClient>, val myselfId: String) {

    init {
        if (nodes[myselfId] != null) {
            throw IllegalArgumentException("Cluster nodes should not contain myself entry")
        }
    }

    val size: Int get() = nodes.size + 1
    fun quorumSize(): Int = (size / 2) + 1
}

data class Node(val host: String, val port: Int)