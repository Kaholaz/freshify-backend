package no.freshify.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ShoppingListEntryAlreadyExistsException extends Exception {
    public ShoppingListEntryAlreadyExistsException() { super("Shopping list entry already exists in the shopping list"); }
}