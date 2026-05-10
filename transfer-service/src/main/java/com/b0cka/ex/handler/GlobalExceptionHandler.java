package com.b0cka.ex.handler;

import com.b0cka.ex.FundsTransferException;
import com.b0cka.ex.InvalidAmount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        log.error("Unexpected error: ", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера");
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(AuthenticationException ex) {
        log.warn("Authentication failed: {}", ex.getMessage());
        return buildResponse(HttpStatus.UNAUTHORIZED, "Требуется аутентификация");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return buildResponse(HttpStatus.FORBIDDEN, "Доступ запрещён");
    }

    @ExceptionHandler(InvalidAmount.class)
    public ResponseEntity<ErrorResponse> handleInvalidAmount(InvalidAmount ex) {
        log.warn("Invalid amount: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(FundsTransferException.class)
    public ResponseEntity<ErrorResponse> handleTransferError(FundsTransferException ex) {
        log.error("Transfer failed: {}", ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(new ErrorResponse(
                        status.value(),
                        message,
                        LocalDateTime.now()
                ));
    }

    public record ErrorResponse(int status, String message, LocalDateTime timestamp) {}
}