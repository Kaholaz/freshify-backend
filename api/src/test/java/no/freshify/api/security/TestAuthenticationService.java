package no.freshify.api.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import no.freshify.api.model.*;
import no.freshify.api.service.HouseholdMemberService;
import no.freshify.api.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestAuthenticationService {
    private final UserService userService = Mockito.mock(UserService.class);
    private final HouseholdMemberService householdMemberService = Mockito.mock(HouseholdMemberService.class);
    private final AuthenticationService authenticationService = new AuthenticationService(userService, householdMemberService);

    private User user;
    private List<HouseholdMember> householdMembers;
    private Authentication authentication;

    private String secret = "abcdefghijklmnopqrstuvwxyzab abcdefghijklmnopqrstuvwxyzab";


    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(authenticationService, "secret", secret);

        user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setFirstName("Test");
        user.setId(1L);

        var memberKey = new HouseholdMemberKey();
        memberKey.setHouseholdId(1L);
        memberKey.setUserId(1L);

        var member = new HouseholdMember();
        member.setId(memberKey);
        member.setRole(HouseholdMemberRole.SUPERUSER);
        member.setHousehold(new Household(1L, "Test"));

        householdMembers = List.of(member);
        authentication = new UserAuthentication(new UserDetailsImpl(user, householdMembers));
    }

    @Test
    public void testJwtTokenContainsSubject() {
        Mockito.when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        Mockito.when(householdMemberService.getHouseHoldMembersByUserId(user.getId())).thenReturn(householdMembers);

        var token = authenticationService.generateToken(authentication);
        assertEquals("test@example.com", authenticationService.getEmailFromToken(token));
    }

    @Test
    public void testJwtTokenContainsAuthorities() {
        Mockito.when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        Mockito.when(householdMemberService.getHouseHoldMembersByUserId(user.getId())).thenReturn(householdMembers);

        var token = authenticationService.generateToken(authentication);
        var auth = authenticationService.getAuthentication(token);

        var authorities = auth.getAuthorities();
        assertEquals(authentication.getAuthorities(), authorities);
    }

    @Test
    public void testJwtTokenIsValid() {
        Mockito.when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        Mockito.when(householdMemberService.getHouseHoldMembersByUserId(user.getId())).thenReturn(householdMembers);

        var token = authenticationService.generateToken(authentication);
        var auth = authenticationService.getAuthentication(token);

        String expectedEmail = ((UserDetailsImpl) authentication.getPrincipal()).getEmail();
        String actualEmail = ((UserDetailsImpl) auth.getPrincipal()).getEmail();
        assertEquals(expectedEmail, actualEmail);
    }

    @Test
    void testJwtWithInvalidSignatureIsRefused() {
        Mockito.when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        Mockito.when(householdMemberService.getHouseHoldMembersByUserId(user.getId())).thenReturn(householdMembers);

        var token = "eyJhbGciOiJIUzI1NiJ9" +
                ".eyJpc3MiOiJzZWxmIiwic3ViIjoidGVzdEBleGFtcGxlLmNvbSIsInNjb3BlIjoiSE9VU0VIT0xEOjFfU1VQRVJVU0VSIiwiaWF0IjoxNjgyMDgxMzc2LCJleHAiOjE2ODIwODE5NzZ9" +
                ".AQ_r0KZPsAtIWMIkgFzgiJoHHKr0O2ALK6hLFCEqRv4";

        assertThrows(SignatureException.class, () -> authenticationService.getAuthentication(token));
    }

    @Test
    void testGetExpirationDate() {
        // Milliseconds are truncated
        Date exp = new Date((System.currentTimeMillis() / 1000) * 1000 + 1000 * 60 * 60);

        Key signKey = Keys.hmacShaKeyFor(secret.getBytes());
        String token = Jwts.builder()
                .signWith(signKey, SignatureAlgorithm.HS256)
                .setExpiration(exp)
                .compact();

        assertEquals(exp, authenticationService.getExpirationDate(token));
    }
}
