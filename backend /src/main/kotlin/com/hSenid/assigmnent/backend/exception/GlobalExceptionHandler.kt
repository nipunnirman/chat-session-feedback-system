package com.hSenid.assigmnent.backend.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.Instant

data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String,
    val timestamp: Instant = Instant.now(),
    val details: List<String>? = null
)

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val errors = ex.bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" }
        return ResponseEntity.badRequest().body(
            ErrorResponse(400, "Validation failed", "One or more fields are invalid", details = errors)
        )
    }

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleNotFound(ex: ResourceNotFoundException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ErrorResponse(404, "Not found", ex.message ?: "Resource not found")
        )

    @ExceptionHandler(AlreadyRespondedException::class)
    fun handleAlreadyResponded(ex: AlreadyRespondedException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.CONFLICT).body(
            ErrorResponse(409, "Already responded", ex.message ?: "Feedback already submitted")
        )

    @ExceptionHandler(FeedbackExpiredException::class)
    fun handleExpired(ex: FeedbackExpiredException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.GONE).body(
            ErrorResponse(410, "Expired", ex.message ?: "Feedback link has expired")
        )

    @ExceptionHandler(ValidationException::class)
    fun handleCustomValidation(ex: ValidationException): ResponseEntity<ErrorResponse> =
        ResponseEntity.badRequest().body(
            ErrorResponse(400, "Invalid request", ex.message ?: "Invalid request")
        )

    @ExceptionHandler(Exception::class)
    fun handleGeneric(ex: Exception): ResponseEntity<ErrorResponse> =
        ResponseEntity.internalServerError().body(
            ErrorResponse(500, "Internal error", "An unexpected error occurred")
        )
}
