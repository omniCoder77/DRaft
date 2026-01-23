package com.ethyllium.http

import jakarta.servlet.ServletException
import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.io.IOException
import java.io.PrintWriter

@WebServlet
class SimpleServlet : HttpServlet() {

    @Throws(ServletException::class, IOException::class)
    protected override fun doGet(req: HttpServletRequest?, resp: HttpServletResponse) {
        resp.contentType = "text/plain"
        resp.writer.write("ok")
    }

    @Throws(ServletException::class, IOException::class)
    protected override fun doPut(req: HttpServletRequest, resp: HttpServletResponse) {
        val key: String? = req.getParameter("key")
        val value: String? = req.getParameter("value")

        resp.contentType = "text/plain"
        val out: PrintWriter = resp.writer

        if (key != null && value != null) {
            out.println("Key: $key")
            out.println("Value: $value")
        } else {
            resp.status = HttpServletResponse.SC_BAD_REQUEST
            out.println("Error: Please provide both 'key' and 'value' parameters.")
        }
    }
}