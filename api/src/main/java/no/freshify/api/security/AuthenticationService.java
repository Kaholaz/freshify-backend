package no.freshify.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import no.freshify.api.model.HouseholdMember;
import no.freshify.api.model.User;
import no.freshify.api.service.HouseholdMemberService;
import no.freshify.api.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service for authentication
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    public static final TemporalAmount TOKEN_DURATION = ChronoUnit.MINUTES.getDuration().multipliedBy(10); // 10 minutes
    private final UserService userService;
    private final HouseholdMemberService householdMemberService;
    @Value("${jwt.secret}")
    private String secret;
    private Key signingKey = null;

    private Key getSigningKey() {
        if (signingKey == null) {
            signingKey = Keys.hmacShaKeyFor(secret.getBytes());
        }
        return signingKey;
    }

    /**
     * Get the current user
     *
     * @param authentication The authentication object
     * @return The current user
     */
    public String generateToken(Authentication authentication) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + TOKEN_DURATION.get(ChronoUnit.SECONDS) * 1000);
        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .subject(authentication.getName())
                .claim("scope", scope)
                .build();
        return Jwts.builder()
                .setClaims(claims.getClaims())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Renew a token
     *
     * @param token The token to renew
     * @return The renewed token
     */
    public String renewToken(String token) {
        User user = userService.getUserByEmail(getEmailFromToken(token));
        List<HouseholdMember> householdRelations = householdMemberService.getHouseHoldMembersByUserId(user.getId());
        UserDetails userDetails = new UserDetailsImpl(user, householdRelations);
        Authentication authentication = new UserAuthentication(userDetails);
        return this.generateToken(authentication);
    }

    /**
     * Get the email from a token
     *
     * @param token The token
     * @return The email
     */
    public String getEmailFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * Get the expiration date of a token
     *
     * @param token The token
     * @return The expiration date
     */
    public Date getExpirationDate(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Authentication getAuthentication(String token) {
        String email = getEmailFromToken(token);
        User user = this.userService.getUserByEmail(email);
        List<HouseholdMember> householdRelations = householdMemberService.getHouseHoldMembersByUserId(user.getId());
        UserDetails userDetails = new UserDetailsImpl(user, householdRelations);

        return new UserAuthentication(userDetails);
    }

    public User getLoggedInUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userService.getUserByEmail(auth.getName());
    }
}
