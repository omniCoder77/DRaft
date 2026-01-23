package com.ethyllium

import com.ethyllium.grpc.GrpcServer
import com.ethyllium.grpc.Service
import com.ethyllium.http.SimpleServlet
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import com.ethyllium.state.Machine

fun main() {
    val port = System.getenv("PORT")?.toInt() ?: throw RuntimeException("PORT environment variable not set")
    val server = Server(port)

    val grpcPort = System.getenv("GRPC_PORT")?.toInt() ?: throw RuntimeException("GRPC_PORT environment variable not set")
    val service = Service()
    val grpcServer = GrpcServer(grpcPort, service)

    val context = ServletContextHandler(ServletContextHandler.SESSIONS)
    context.contextPath = "/"
    server.handler = context

    context.addServlet(ServletHolder(SimpleServlet()), "/")

    val services = System.getenv("MACHINES").split(',')
    Machine.create(services)

    server.start()
    grpcServer.start()
    grpcServer.blockUntilShutdown()
    server.join()
}