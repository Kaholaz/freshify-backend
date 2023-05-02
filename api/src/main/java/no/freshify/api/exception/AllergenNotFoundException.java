package no.freshify.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.OK)
public class AllergenNotFoundException extends Exception {
    public AllergenNotFoundException() { super("Allergen not found");}
}
