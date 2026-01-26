package com.ethyllium.transport.http

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.PrintWriter
import java.io.StringWriter

class HealthServletTest {

    private val servlet = HealthServlet()

    @Test
    fun `doGet should write ok`() {
        val req = mockk<HttpServletRequest>(relaxed = true)
        val resp = mockk<HttpServletResponse>(relaxed = true)
        val stringWriter = StringWriter()
        val writer = PrintWriter(stringWriter)

        every { resp.writer } returns writer

        val method = HealthServlet::class.java.getDeclaredMethod("doGet", HttpServletRequest::class.java, HttpServletResponse::class.java)
        method.isAccessible = true
        method.invoke(servlet, req, resp)

        Assertions.assertEquals("ok", stringWriter.toString())
    }

    @Test
    fun `doPut should return 400 if params missing`() {
        val req = mockk<HttpServletRequest>(relaxed = true)
        val resp = mockk<HttpServletResponse>(relaxed = true)
        val writer = PrintWriter(StringWriter())
        every { req.getParameter("key") } returns null
        every { resp.writer } returns writer

        val method = HealthServlet::class.java.getDeclaredMethod("doPut", HttpServletRequest::class.java, HttpServletResponse::class.java)
        method.isAccessible = true
        method.invoke(servlet, req, resp)

        verify { resp.status = HttpServletResponse.SC_BAD_REQUEST }
    }
}