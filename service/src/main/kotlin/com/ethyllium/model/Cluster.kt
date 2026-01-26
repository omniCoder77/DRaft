package com.ethyllium.model

import com.ethyllium.transport.RaftClient

/**
 * Represents a collection of machines in a distributed system. This is static and does not change during runtime.
 * @property nodes Map of key as nodeId and value as corresponding [Node].
 */
data class Cluster (val nodes: Map<String, RaftClient>, val myself: String){
    val size: Int get() = nodes.size + 1
    fun quorumSize(): Int = (size / 2) + 1
}

data class Node(val host: String, val port: Int)