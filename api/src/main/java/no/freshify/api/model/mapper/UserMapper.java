package no.freshify.api.model.mapper;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import no.freshify.api.model.User;
import no.freshify.api.model.dto.CreateUser;
import no.freshify.api.model.dto.LoginUser;
import no.freshify.api.model.dto.UserFull;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring", injectionStrategy = org.mapstruct.InjectionStrategy.CONSTRUCTOR)
public abstract class UserMapper {
    @Setter
    @Autowired
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Named("encodePassword")
    public String encodePassword(String password) {
        if (password == null) return null;
        return passwordEncoder.encode(password);
    }

    @Mappings({
            @Mapping(target = "password", qualifiedByName = "encodePassword"),
    })
    public abstract User fromCreateUser(CreateUser user);

    @Mappings({
            @Mapping(target = "password", qualifiedByName = "encodePassword"),
    })
    public abstract User fromLoginUser(LoginUser user);

    public abstract UserFull toUserFull(User user);
}
