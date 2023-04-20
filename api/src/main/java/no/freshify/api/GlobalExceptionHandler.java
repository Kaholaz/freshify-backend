package no.freshify.api;

import no.freshify.api.exception.HouseholdMemberAlreadyExistsException;
import no.freshify.api.exception.HouseholdNotFoundException;
import no.freshify.api.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(HouseholdNotFoundException.class)
    public ResponseEntity<Object> handleHouseholdNotFoundException(HouseholdNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(HouseholdMemberAlreadyExistsException.class)
    public ResponseEntity<Object> handleHouseholdMemberAlreadyExistsException(HouseholdMemberAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}
