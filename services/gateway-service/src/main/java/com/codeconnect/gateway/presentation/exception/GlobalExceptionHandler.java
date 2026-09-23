package com.codeconnect.gateway.presentation.exception;

import com.codeconnect.gateway.domain.exception.AccountBannedException;
import com.codeconnect.gateway.domain.exception.EmailAlreadyExistsException;
import com.codeconnect.gateway.domain.exception.InvalidCredentialsException;
import com.codeconnect.gateway.domain.exception.UnauthorizedException;
import com.codeconnect.gateway.domain.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.net.URI;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Centralized exception handler producing RFC 7807 Problem Details envelopes.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final URI CONFLICT_TYPE = URI.create("https://codeconnect.dev/errors/email-conflict");
    private static final URI VALIDATION_ERROR_TYPE = URI.create("https://codeconnect.dev/errors/validation-error");
    private static final URI INVALID_CREDENTIALS_TYPE = URI.create("https://codeconnect.dev/errors/invalid-credentials");
    private static final URI ACCOUNT_BANNED_TYPE = URI.create("https://codeconnect.dev/errors/account-banned");
    private static final URI UNAUTHORIZED_TYPE = URI.create("https://codeconnect.dev/errors/unauthorized");
    private static final URI INTERNAL_ERROR_TYPE = URI.create("https://codeconnect.dev/errors/internal-error");

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        log.warn("Handling EmailAlreadyExistsException: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setType(CONFLICT_TYPE);
        problem.setTitle("Email Conflict");
        problem.setProperty("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.CONFLICT)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(problem);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ProblemDetail> handleInvalidCredentials(InvalidCredentialsException ex) {
        log.warn("Handling InvalidCredentialsException: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        problem.setType(INVALID_CREDENTIALS_TYPE);
        problem.setTitle("Invalid Credentials");
        problem.setProperty("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(problem);
    }

    @ExceptionHandler(AccountBannedException.class)
    public ResponseEntity<ProblemDetail> handleAccountBanned(AccountBannedException ex) {
        log.warn("Handling AccountBannedException: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        problem.setType(ACCOUNT_BANNED_TYPE);
        problem.setTitle("Account Banned");
        problem.setProperty("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(problem);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ProblemDetail> handleUnauthorized(UnauthorizedException ex) {
        log.warn("Handling UnauthorizedException: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        problem.setType(UNAUTHORIZED_TYPE);
        problem.setTitle("Unauthorized");
        problem.setProperty("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(problem);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ProblemDetail> handleValidationException(ValidationException ex) {
        log.warn("Handling ValidationException: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setType(VALIDATION_ERROR_TYPE);
        problem.setTitle("Invalid Request");
        problem.setProperty("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(problem);
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ProblemDetail> handleBindException(WebExchangeBindException ex) {
        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(
                FieldError::getField,
                error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid value",
                (first, second) -> first
            ));

        log.warn("Handling WebExchangeBindException: {}", fieldErrors);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed for request fields");
        problem.setType(VALIDATION_ERROR_TYPE);
        problem.setTitle("Validation Failed");
        problem.setProperty("errors", fieldErrors);
        problem.setProperty("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(Exception ex) {
        log.error("Unhandled exception intercepted by GlobalExceptionHandler", ex);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected internal error occurred"
        );
        problem.setType(INTERNAL_ERROR_TYPE);
        problem.setTitle("Internal Server Error");
        problem.setProperty("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(problem);
    }
}
