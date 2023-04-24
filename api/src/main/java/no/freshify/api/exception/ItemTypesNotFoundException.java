package no.freshify.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ItemTypesNotFoundException extends Exception {
    public ItemTypesNotFoundException() {
        super("No item types matched the search query");
    }
}
