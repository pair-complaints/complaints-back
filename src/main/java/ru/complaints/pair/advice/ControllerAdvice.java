package ru.complaints.pair.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.complaints.pair.exception.AuthException;
import ru.complaints.pair.exception.DuplicatedUsernameException;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<String> handleAuthException(AuthException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler(DuplicatedUsernameException.class)
    public ResponseEntity<String> handleDuplicatedUsernameException(DuplicatedUsernameException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
