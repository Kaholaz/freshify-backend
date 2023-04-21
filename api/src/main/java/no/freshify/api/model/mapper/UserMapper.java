package no.freshify.api.model.mapper;

import no.freshify.api.model.User;
import no.freshify.api.model.dto.CreateUser;
import no.freshify.api.model.dto.UserFull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper
public abstract class UserMapper {
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Named("encodePassword")
    public String encodePassword(String password) {
        if (password == null) return null;
        return passwordEncoder.encode(password);
    }

    @Mappings({
            @Mapping(target = "password", qualifiedByName = "encodePassword"),
    })
    public abstract User fromCreateUser(CreateUser user);

    public abstract UserFull toUserFull(User user);
}
