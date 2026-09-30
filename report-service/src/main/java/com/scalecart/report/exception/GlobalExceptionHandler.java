package com.scalecart.report.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String KEY_TIMESTAMP = "timestamp";
    private static final String KEY_STATUS = "status";
    private static final String KEY_ERROR = "error";
    private static final String KEY_MESSAGES = "messages";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException ex) {
        List<String> messages = ex.getBindingResult().getFieldErrors()
                .stream().map(FieldError::getDefaultMessage)
                .toList();
        return ResponseEntity.badRequest().body(
                errorBody(400, "Validation Failed", messages));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                errorBody(500, "Report Generation Failed", List.of(ex.getMessage())));
    }

    private static Map<String, Object> errorBody(int status, String error, List<String> messages) {
        return Map.of(
                KEY_TIMESTAMP, LocalDateTime.now(ZoneId.systemDefault()).toString(),
                KEY_STATUS, status,
                KEY_ERROR, error,
                KEY_MESSAGES, messages
        );
    }
}
