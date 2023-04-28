package no.freshify.api.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import no.freshify.api.model.HouseholdMember;
import no.freshify.api.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * User details implementation
 */
@Getter
@RequiredArgsConstructor
public class UserDetailsImpl implements UserDetails {
    private final Long id;
    private final String email;
    private final String password;
    private final String firstName;
    private final List<HouseholdMember> householdRelations;

    /**
     * Create user details from a user entity
     *
     * @param user The user entity
     */
    public UserDetailsImpl(User user, List<HouseholdMember> householdRelations) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.firstName = user.getFirstName();
        this.householdRelations = householdRelations;
    }

    /**
     * Get the authorities of the user (admin or user)
     *
     * @return The authorities of the user
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return householdRelations.stream().map(m -> String.format("HOUSEHOLD:%d_%s", m.getHousehold().getId(),
                m.getRole().name())).map(SimpleGrantedAuthority::new).toList();
    }

    /**
     * Get the username of the user (email)
     *
     * @return The username of the user
     */
    @Override
    public String getUsername() {
        return getEmail();
    }

    /**
     * Check if the account is not expired (always true)
     *
     * @return True if the account is not expired
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Check if the account is not locked (always true)
     *
     * @return True if the account is not locked
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Check if the credentials are not expired (always true)
     *
     * @return True if the credentials are not expired
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Check if the account is enabled (always true)
     *
     * @return True if the account is enabled
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
