package no.freshify.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class HouseholdMemberAlreadyExistsException extends Exception {
    public HouseholdMemberAlreadyExistsException() {
        super("Household member already exists");
    }
}
