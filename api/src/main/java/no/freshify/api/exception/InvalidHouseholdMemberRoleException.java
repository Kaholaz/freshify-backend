package no.freshify.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidHouseholdMemberRoleException extends Exception {
    public InvalidHouseholdMemberRoleException() {
        super("Invalid household member role");
    }
}
