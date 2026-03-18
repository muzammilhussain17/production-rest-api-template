package com.example.api.exception;

import com.example.api.filter.CorrelationIdFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Resource Not Found");
        problemDetail.setType(URI.create("https://api.example.com/errors/not-found"));
        enrichProblemDetail(problemDetail, request);
        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed for one or more fields.");
        problemDetail.setTitle("Bad Request");
        problemDetail.setType(URI.create("https://api.example.com/errors/validation-failed"));

        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        problemDetail.setProperty("invalid_params", errors);
        enrichProblemDetail(problemDetail, request);
        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleAllUncaughtException(Exception ex, HttpServletRequest request) {
        log.error("Unknown error occurred", ex);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setType(URI.create("https://api.example.com/errors/internal-server-error"));
        enrichProblemDetail(problemDetail, request);
        return problemDetail;
    }

    private void enrichProblemDetail(ProblemDetail problemDetail, HttpServletRequest request) {
        problemDetail.setProperty("timestamp", Instant.now().toString());
        String correlationId = request.getHeader(CorrelationIdFilter.CORRELATION_ID_HEADER);
        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString();
        }
        problemDetail.setProperty("traceId", correlationId);
    }
}
