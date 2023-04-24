package no.freshify.api.security;

import no.freshify.api.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestPermissionEvaluator {
    private final PermissionEvaluator permissionEvaluator = new PermissionEvaluatorImpl();
    private Authentication authentication;

    private User user;
    private Household household;
    private List<HouseholdMember> householdMembers;

    @BeforeEach
    public void setup() {
        user = new User();
        user.setId(1L);
        user.setEmail("test");

        household = new Household();
        household.setId(1L);

        HouseholdMember member = new HouseholdMember();
        HouseholdMemberKey key = new HouseholdMemberKey();
        key.setHouseholdId(1L);
        key.setUserId(1L);

        member.setId(key);
        member.setHousehold(household);
        member.setUser(user);
        member.setRole(HouseholdMemberRole.SUPERUSER);

        householdMembers = List.of(member);
        authentication = new UserAuthentication(new UserDetailsImpl(user, householdMembers));
    }


    @Test
    public void testHasPermissionForSuperuser() {
        assertTrue(permissionEvaluator.hasPermission(authentication, 1L, "household", "superuser"));
    }

    @Test
    public void testHasPermissionForAnyone() {
        assertTrue(permissionEvaluator.hasPermission(authentication, 1L, "household", ""));
    }

    @Test
    public void testHasntPermission() {
        assertFalse(permissionEvaluator.hasPermission(authentication, 2L, "household", ""));
    }
}
