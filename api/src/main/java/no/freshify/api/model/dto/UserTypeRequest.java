package no.freshify.api.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserTypeRequest {
    private Long userId;
    private String userType;
}
