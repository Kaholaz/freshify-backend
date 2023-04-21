package no.freshify.api.security;

import lombok.RequiredArgsConstructor;
import no.freshify.api.model.HouseholdMember;
import no.freshify.api.model.User;
import no.freshify.api.service.HouseholdMemberService;
import no.freshify.api.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for user details
 */
@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {
    private final UserService userService;
    private final HouseholdMemberService householdMemberService;

    /**
     * Load user details by username
     *
     * @param username the username identifying the user whose data is required.
     * @return a fully populated user record (never null)
     * @throws UsernameNotFoundException if the user could not be found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.getUserByEmail(username);
        List<HouseholdMember> householdRelations = householdMemberService.getHouseHoldMembersByUserId(user.getId());
        return new UserDetailsImpl(user, householdRelations);
    }
}
