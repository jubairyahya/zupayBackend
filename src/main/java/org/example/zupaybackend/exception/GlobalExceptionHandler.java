package org.example.zupaybackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntime(RuntimeException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getMessage());

        HttpStatus status = switch (ex.getMessage()) {
            case "No outstanding bills"  -> HttpStatus.NOT_FOUND;
            case "Already paid"          -> HttpStatus.CONFLICT;
            case "Insufficient balance"  -> HttpStatus.BAD_REQUEST;
            case "Bill not found"        -> HttpStatus.NOT_FOUND;
            default                      -> HttpStatus.INTERNAL_SERVER_ERROR;
        };

        return ResponseEntity.status(status).body(body);
    }
}