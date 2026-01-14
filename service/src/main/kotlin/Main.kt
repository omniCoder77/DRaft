package com.ethyllium

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder


object MainApp {
    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val port = System.getenv("PORT")?.toInt() ?: throw RuntimeException("PORT environment variable not set")
        val server = Server(port)

        val context = ServletContextHandler(ServletContextHandler.SESSIONS)
        context.contextPath = "/"
        server.handler = context

        context.addServlet(ServletHolder(SimpleServlet()), "/simple")

        server.start()
        server.join()
    }
}