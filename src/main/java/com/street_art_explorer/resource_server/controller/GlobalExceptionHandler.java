package com.street_art_explorer.resource_server.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.OffsetDateTime;
import java.util.stream.Collectors;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    public record ErrorResponse(
            String code,
            String message,
            String path,
            OffsetDateTime timestamp
    ) {
    }

    private ErrorResponse body(String code, String message, String path) {
        return new ErrorResponse(code, message, path, OffsetDateTime.now());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex,
                                                            org.springframework.web.context.request.WebRequest req) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(body("UNAUTHORIZED", msg(ex), path(req)));
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex,
                                                               org.springframework.web.context.request.WebRequest req) {
        return ResponseEntity.badRequest().body(body("BAD_REQUEST", msg(ex), path(req)));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResponseStatusException ex,
                                                        org.springframework.web.context.request.WebRequest req) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(body("FORBIDDEN", msg(ex), path(req)));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(EntityNotFoundException ex,
                                                        org.springframework.web.context.request.WebRequest req) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(body("NOT_FOUND", msg(ex), path(req)));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleConflict(DataIntegrityViolationException ex,
                                                        org.springframework.web.context.request.WebRequest req) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(body("DATA_CONFLICT", "Data conflict", path(req)));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDbDown(DataAccessException ex,
                                                      org.springframework.web.context.request.WebRequest req) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(body("DATA_UNAVAILABLE", "Data service is not available", path(req)));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  org.springframework.web.context.request.WebRequest req) {
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest().body(
                body("VALIDATION_ERROR", details, path(req))
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex,
                                                                   org.springframework.web.context.request.WebRequest req) {
        String details = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest().body(
                body("VALIDATION_ERROR", details, path(req))
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                            org.springframework.web.context.request.WebRequest req) {
        String details = "Invalid parameter: '" + ex.getName();
        return ResponseEntity.badRequest().body(body("TYPE_MISMATCH", details, path(req)));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex,
                                                       org.springframework.web.context.request.WebRequest req) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(body("INTERNAL_ERROR", "Internal server error", path(req)));
    }

    private String msg(Exception ex) {
        return ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName();
    }

    private String path(org.springframework.web.context.request.WebRequest req) {
        String desc = req.getDescription(false);
        return desc != null && desc.startsWith("uri=") ? desc.substring(4) : desc;
    }
}
