package no.freshify.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class HouseholdNotFoundException extends Exception {
    public HouseholdNotFoundException() {
        super("Household not found");
    }
}
