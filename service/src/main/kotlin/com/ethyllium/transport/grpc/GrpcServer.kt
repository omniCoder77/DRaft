package com.ethyllium.transport.grpc

import io.grpc.BindableService
import io.grpc.ServerBuilder

class GrpcServer(port: Int, service: BindableService) {

    private val server = ServerBuilder.forPort(port).addService(service).build()

    fun start() {
        server.start()
        Runtime.getRuntime().addShutdownHook(Thread {
            this@GrpcServer.stop()
        })
    }

    fun stop() {
        server.shutdown()
    }

    fun blockUntilShutdown() {
        server.awaitTermination()
    }

}