package no.freshify.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ItemDoesNotBelongToHouseholdException extends Exception {
    public ItemDoesNotBelongToHouseholdException() {
        super("Item does not belong to household");
    }
}
