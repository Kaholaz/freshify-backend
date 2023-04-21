package no.freshify.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserDoesNotBelongToHouseholdException extends Exception {
    public UserDoesNotBelongToHouseholdException() {
        super("User does not belong to household");
    }
}

