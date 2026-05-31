package io.github.emersondll.transactions.controller;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.github.emersondll.transactions.exception.AccountNotFoundException;
import io.github.emersondll.transactions.exception.InvalidTransactionException;
import io.github.emersondll.transactions.exception.OperationTypeNotFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 * Global exception handler providing consistent, structured error responses
 * for all REST controllers.
 *
 * <p>Centralises error handling to prevent sensitive information leakage and
 * ensure uniform error contracts across all API endpoints.</p>
 *
 * <p>Error response record: {@link ErrorResponse} — includes an error code,
 * a human-readable message, and the timestamp of the failure.</p>
 *
 * @author Emerson Lima
 * @version 1.0
 * @since 1.0.0
 */
@RestControllerAdvice
@Slf4j
public class Handler {

    /**
     * Handles not-found exceptions for accounts and operation types.
     * Returns {@code 404 NOT FOUND} with the exception message.
     *
     * @param exception the thrown {@link AccountNotFoundException} or
     *                  {@link OperationTypeNotFoundException}
     * @return {@link ResponseEntity} with {@code 404} status and error details
     */
    @ExceptionHandler({AccountNotFoundException.class, OperationTypeNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException exception) {
        log.warn("Resource not found. message={}", exception.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("NOT_FOUND", exception.getMessage(), LocalDateTime.now()));
    }

    /**
     * Handles invalid transaction data exceptions.
     * Returns {@code 400 BAD REQUEST} with the validation failure message.
     *
     * @param exception the thrown {@link InvalidTransactionException}
     * @return {@link ResponseEntity} with {@code 400} status and error details
     */
    @ExceptionHandler(InvalidTransactionException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTransaction(InvalidTransactionException exception) {
        log.warn("Invalid transaction. message={}", exception.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("INVALID_TRANSACTION", exception.getMessage(), LocalDateTime.now()));
    }

    /**
     * Handles Bean Validation failures from {@code @Valid} annotations on request bodies.
     * Returns {@code 400 BAD REQUEST} with a summary of validation errors.
     *
     * @param exception the thrown {@link MethodArgumentNotValidException}
     * @return {@link ResponseEntity} with {@code 400} status and validation error summary
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
        String details = exception.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .reduce("", (a, b) -> a.isEmpty() ? b : a + "; " + b);

        log.warn("Validation failed. errors={}", details);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("VALIDATION_FAILED", details, LocalDateTime.now()));
    }

    /**
     * Fallback handler for any unexpected exception.
     * Returns {@code 500 INTERNAL SERVER ERROR} without exposing internal details.
     *
     * @param exception the unexpected exception
     * @return {@link ResponseEntity} with {@code 500} status and a generic message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception exception) {
        log.error("Unexpected error occurred", exception);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        "INTERNAL_SERVER_ERROR",
                        "An unexpected error occurred. Please try again later.",
                        LocalDateTime.now()
                ));
    }

    /**
     * Immutable record representing a structured API error response.
     *
     * @param code      machine-readable error code (e.g., {@code NOT_FOUND})
     * @param message   human-readable description of the error
     * @param timestamp UTC timestamp of when the error occurred
     */
    public record ErrorResponse(String code, String message, LocalDateTime timestamp) {
    }
}
