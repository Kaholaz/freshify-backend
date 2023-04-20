package no.freshify.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ItemTypeNotFoundException extends Exception {
    public ItemTypeNotFoundException() {
        super("Item type not found");
    }
}