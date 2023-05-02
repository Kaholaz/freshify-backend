package no.freshify.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RecipeCategoryNotFoundException extends Exception {
    public RecipeCategoryNotFoundException() {
        super("Recipe category not found");
    }
}
