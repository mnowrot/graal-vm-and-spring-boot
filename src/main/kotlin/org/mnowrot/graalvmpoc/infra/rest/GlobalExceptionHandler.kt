package org.mnowrot.graalvmpoc.infra.rest

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

@RestControllerAdvice
class GlobalExceptionHandler {

    companion object {
        private val log = KotlinLogging.logger {}
    }

    data class ApiError(
        val timestamp: Instant = Instant.now(),
        val status: Int,
        val error: String,
        val message: String?,
        val path: String
    )

    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatus(ex: ResponseStatusException, request: HttpServletRequest): ResponseEntity<ApiError> {
        val status = HttpStatus.valueOf(ex.statusCode.value())
        val error = ApiError(
            status = status.value(),
            error = status.reasonPhrase,
            message = ex.reason ?: ex.message,
            path = request.requestURI
        )
        logByStatus(status, "HTTP ${status.value()} ${status.reasonPhrase} at ${request.requestURI}: ${error.message}")
        return ResponseEntity.status(status).body(error)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<ApiError> {
        return badRequestResponse(ex, request)
    }

    @ExceptionHandler(BindException::class)
    fun handleBindException(ex: BindException, request: HttpServletRequest): ResponseEntity<ApiError> {
        return badRequestResponse(ex, request)
    }

    private fun badRequestResponse(
        ex: BindException,
        request: HttpServletRequest
    ): ResponseEntity<ApiError> {
        val status = HttpStatus.BAD_REQUEST
        val message = ex.bindingResult.fieldErrors
            .joinToString(
                separator = "; ",
                transform = { "${it.field}: ${it.defaultMessage}" }
            )
            .ifBlank { ex.message }
        val error = ApiError(
            status = status.value(),
            error = status.reasonPhrase,
            message = message,
            path = request.requestURI
        )
        log.warn { "HTTP 400 Bad Request at ${request.requestURI}: $message" }
        return ResponseEntity.status(status).body(error)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleNotReadable(ex: HttpMessageNotReadableException, request: HttpServletRequest): ResponseEntity<ApiError> {
        val status = HttpStatus.BAD_REQUEST
        val message = ex.mostSpecificCause.message ?: ex.message ?: "Malformed JSON request"
        val error = ApiError(
            status = status.value(),
            error = status.reasonPhrase,
            message = message,
            path = request.requestURI
        )
        log.warn(ex) { "HTTP 400 Bad Request at ${request.requestURI}: $message" }
        return ResponseEntity.status(status).body(error)
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneric(ex: Exception, request: HttpServletRequest): ResponseEntity<ApiError> {
        val status = HttpStatus.INTERNAL_SERVER_ERROR
        val error = ApiError(
            status = status.value(),
            error = status.reasonPhrase,
            message = "Unexpected error occurred",
            path = request.requestURI
        )
        log.error(ex) { "HTTP 500 Internal Server Error at ${request.requestURI}: ${ex.message}" }
        return ResponseEntity.status(status).body(error)
    }

    private fun logByStatus(status: HttpStatus, msg: String) {
        if (status.is5xxServerError) {
            log.error { msg }
        } else {
            log.warn { msg }
        }
    }
}
