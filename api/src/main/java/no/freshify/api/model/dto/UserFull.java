package no.freshify.api.model.dto;

import lombok.Getter;
import lombok.Setter;
import no.freshify.api.model.User;

@Getter
@Setter
public class UserFull {
    private long id;
    private String firstName;
    private String email;

    public UserFull(User user) {
        this.setId(user.getId());
        this.setFirstName(user.getFirstName());
        this.setEmail(user.getEmail());
    }
}
