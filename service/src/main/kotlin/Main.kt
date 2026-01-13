package com.ethyllium

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder


object MainApp {
    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val server = Server(8080)

        val context = ServletContextHandler(ServletContextHandler.SESSIONS)
        context.contextPath = "/"
        server.handler = context

        context.addServlet(ServletHolder(SimpleServlet()), "/simple")

        println("Server starting at http://localhost:8080/simple")
        server.start()
        server.join()
    }
}