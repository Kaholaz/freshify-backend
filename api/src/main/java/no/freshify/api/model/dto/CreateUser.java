package no.freshify.api.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUser {
    private String email;
    private String password;
    private String firstName;
}
