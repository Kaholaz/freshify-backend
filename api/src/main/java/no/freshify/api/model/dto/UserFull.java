package no.freshify.api.model.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserFull {
    private Long id;
    private String firstName;
    private String email;
}
