package com.ethyllium

import com.ethyllium.model.Cluster
import com.ethyllium.model.Node
import com.ethyllium.persistence.FileStorage
import com.ethyllium.protocol.ConsensusModule
import com.ethyllium.protocol.ElectionTimer
import com.ethyllium.protocol.RaftState
import com.ethyllium.transport.RaftClient
import com.ethyllium.transport.grpc.GrpcRaftClient
import com.ethyllium.transport.grpc.GrpcServer
import com.ethyllium.transport.grpc.RaftGrpcService
import com.ethyllium.transport.http.HealthServlet
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import java.io.File

fun main() {
    val nodeId = System.getenv("NODE_ID") ?: "node-1"
    val grpcPort = System.getenv("GRPC_PORT")?.toInt() ?: 9090
    val httpPort = System.getenv("PORT")?.toInt() ?: 8080
    val storageDir = File("./data/$nodeId").apply { mkdirs() }

    val machineMetadata = System.getenv("MACHINE_METADATA")
        ?: throw IllegalArgumentException("MACHINE_METADATA required")

    val peers = mutableMapOf<String, RaftClient>()
    machineMetadata.split(":").forEach { entry ->
        val (id, host, portStr) = entry.split(",")
        if (id != nodeId) {
            peers[id] = GrpcRaftClient(Node(host, portStr.toInt()))
        }
    }

    val cluster = Cluster(nodes = peers, myself = nodeId)
    val storage = FileStorage(storageDir)
    val runtimeState = RaftState()
    val timer = ElectionTimer()

    val consensus = ConsensusModule(
        nodeId,
        cluster,
        storage,
        runtimeState,
        timer
    )

    timer.setElectionCallback { consensus.onElectionTimeout() }
    timer.resetElectionTimer()

    val grpcServer = GrpcServer(grpcPort, RaftGrpcService(consensus))
    grpcServer.start()

    val httpServer = Server(httpPort)
    val context = ServletContextHandler(ServletContextHandler.SESSIONS)
    context.addServlet(ServletHolder(HealthServlet()), "/")
    httpServer.handler = context
    httpServer.start()

    println("Raft Node $nodeId started on gRPC=$grpcPort, HTTP=$httpPort")
    httpServer.join()
}