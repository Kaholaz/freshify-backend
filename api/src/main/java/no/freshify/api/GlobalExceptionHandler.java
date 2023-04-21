package no.freshify.api;

import no.freshify.api.exception.*;
import org.apache.coyote.Response;
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

    @ExceptionHandler(ItemTypeNotFoundException.class)
    public ResponseEntity<Object> handleItemTypeNotFoundException(ItemTypeNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<Object> handleItemNotFoundException(ItemNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalItemStatusException.class)
    public ResponseEntity<Object> handleItemNotFoundException(IllegalItemStatusException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(ItemDoesNotBelongToHouseholdException.class)
    public ResponseEntity<Object> handleItemDoesNotBelongToHouseholdException(ItemDoesNotBelongToHouseholdException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalItemParameterException.class)
    public ResponseEntity<Object> handleIllegalItemParameterException(IllegalItemParameterException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(UserDoesNotBelongToHouseholdException.class)
    public ResponseEntity<Object> handleUserDoesNotBelongToHouseholdException(UserDoesNotBelongToHouseholdException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidHouseholdMemberRoleException.class)
    public ResponseEntity<Object> handleInvalidHouseholdMemberRoleException(InvalidHouseholdMemberRoleException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
