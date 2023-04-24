package no.freshify.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ShoppingListEntryNotFoundException extends Exception {
    public ShoppingListEntryNotFoundException() { super("A shopping list entry with given type was not found in the given household"); }
}
