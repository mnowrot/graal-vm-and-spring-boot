package org.mnowrot.graalvmpoc.infra.rest

import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.http.HttpStatus
import org.springframework.http.HttpHeaders
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.http.HttpInputMessage
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.validation.BindException
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.core.MethodParameter
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.server.ResponseStatusException
import java.io.InputStream
import java.lang.reflect.Method
import kotlin.test.Test

class GlobalExceptionHandlerTest {

    private val handler = GlobalExceptionHandler()

    private fun request(path: String = "/test"): HttpServletRequest = MockHttpServletRequest().apply {
        requestURI = path
    }

    private fun dummyInput(): HttpInputMessage = object : HttpInputMessage {
        override fun getBody(): InputStream = InputStream.nullInputStream()
        override fun getHeaders(): HttpHeaders = HttpHeaders()
    }

    @Test
    fun `handle ResponseStatusException for 404 and 500`() {
        val rse404 = ResponseStatusException(HttpStatus.NOT_FOUND, "Not here")
        val res404 = handler.handleResponseStatus(rse404, request("/missing"))
        assertEquals(404, res404.statusCode.value())
        assertEquals("Not Found", res404.body!!.error)
        assertEquals("/missing", res404.body!!.path)

        val rse500 = ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Boom")
        val res500 = handler.handleResponseStatus(rse500, request("/boom"))
        assertEquals(500, res500.statusCode.value())
        assertEquals("/boom", res500.body!!.path)
    }

    data class Dummy(val name: String?)
    data class WithField(var field: String? = null)

    private fun methodParameterForDummy(): MethodParameter {
        val method: Method = DummyController::class.java.getMethod("accept", Dummy::class.java)
        return MethodParameter(method, 0)
    }

    class DummyController {
        fun accept(@RequestBody dummy: Dummy) {}
    }

    @Test
    fun `handle MethodArgumentNotValidException with field errors`() {
        val target = Dummy(name = "")
        val binding = BeanPropertyBindingResult(target, "dummy")
        binding.rejectValue("name", "NotBlank", "must not be blank")
        val ex = MethodArgumentNotValidException(methodParameterForDummy(), binding)
        val res = handler.handleMethodArgumentNotValid(ex, request("/dummy"))
        assertEquals(400, res.statusCode.value())
        assertTrue(res.body!!.message!!.contains("name"))
        assertEquals("/dummy", res.body!!.path)
    }

    @Test
    fun `handle BindException with field errors and fallback message`() {
        val any = WithField()
        val binding = BeanPropertyBindingResult(any, "any")
        // No field errors initially -> will fallback to ex.message
        val exNoFields = BindException(binding)
        val resNoFields = handler.handleBindException(exNoFields, request("/bind"))
        assertEquals(400, resNoFields.statusCode.value())
        assertEquals("/bind", resNoFields.body!!.path)

        // With field error
        binding.rejectValue("field", "invalid", "bad")
        val exWithField = BindException(binding)
        val resWithField = handler.handleBindException(exWithField, request("/bind2"))
        assertEquals(400, resWithField.statusCode.value())
        assertTrue(resWithField.body!!.message!!.contains("field"))
    }

    @Test
    fun `handle HttpMessageNotReadableException with mostSpecificCause and without`() {
        val cause = IllegalArgumentException("bad json")
        val exWithCause = HttpMessageNotReadableException("boom", cause, dummyInput())
        val res1 = handler.handleNotReadable(exWithCause, request("/nr1"))
        assertEquals(400, res1.statusCode.value())
        assertTrue(res1.body!!.message!!.contains("bad json"))

        val exNoCause = HttpMessageNotReadableException("boom", null, dummyInput())
        val res2 = handler.handleNotReadable(exNoCause, request("/nr2"))
        assertEquals(400, res2.statusCode.value())
        assertTrue(res2.body!!.message!!.contains("boom"))
    }

    @Test
    fun `handle generic Exception returns 500`() {
        val ex = RuntimeException("oops")
        val res = handler.handleGeneric(ex, request("/gen"))
        assertEquals(500, res.statusCode.value())
        assertEquals("/gen", res.body!!.path)
        assertEquals("Unexpected error occurred", res.body!!.message)
    }
}
