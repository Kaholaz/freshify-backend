package no.freshify.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class HouseholdRecipeNotFoundException extends Exception {
    public HouseholdRecipeNotFoundException() {
        super("Household or recipe not found");
    }
}
