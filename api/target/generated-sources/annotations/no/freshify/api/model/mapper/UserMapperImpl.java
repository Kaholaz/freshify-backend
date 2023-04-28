package no.freshify.api.model.mapper;

import javax.annotation.processing.Generated;
import no.freshify.api.model.User;
import no.freshify.api.model.dto.CreateUser;
import no.freshify.api.model.dto.UpdateUser;
import no.freshify.api.model.dto.UserFull;
import no.freshify.api.model.dto.UserId;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-04-28T10:55:48+0200",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.6 (Oracle Corporation)"
)
public class UserMapperImpl extends UserMapper {

    @Override
    public User fromCreateUser(CreateUser user) {
        if ( user == null ) {
            return null;
        }

        User user1 = new User();

        user1.setPassword( encodePassword( user.getPassword() ) );
        user1.setFirstName( user.getFirstName() );
        user1.setEmail( user.getEmail() );

        return user1;
    }

    @Override
    public UserFull toUserFull(User user) {
        if ( user == null ) {
            return null;
        }

        UserFull userFull = new UserFull();

        userFull.setId( user.getId() );
        userFull.setFirstName( user.getFirstName() );
        userFull.setEmail( user.getEmail() );

        return userFull;
    }

    @Override
    public UserId toUserId(User user) {
        if ( user == null ) {
            return null;
        }

        UserId userId = new UserId();

        userId.userId = user.getId();

        return userId;
    }

    @Override
    public User toUser(UpdateUser user) {
        if ( user == null ) {
            return null;
        }

        User user1 = new User();

        user1.setId( user.getId() );
        user1.setFirstName( user.getFirstName() );
        user1.setEmail( user.getEmail() );
        user1.setPassword( user.getPassword() );

        return user1;
    }
}
