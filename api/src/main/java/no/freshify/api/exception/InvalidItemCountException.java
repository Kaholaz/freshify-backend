package no.freshify.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidItemCountException extends Exception {
    public InvalidItemCountException() {
        super("Invalid operation. Item count set to non positive number");
    }
}
