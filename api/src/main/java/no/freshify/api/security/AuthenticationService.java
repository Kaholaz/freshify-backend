package no.freshify.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import no.freshify.api.model.User;
import no.freshify.api.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.Date;
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
    private final Key signingKey = Keys.hmacShaKeyFor("VerySecretKey".getBytes());

    /**
     * Get the current user
     *
     * @param authentication The authentication object
     * @return The current user
     */
    public String generateToken(Authentication authentication) {
        Instant now = Instant.now();
        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(TOKEN_DURATION))
                .subject(authentication.getName())
                .claim("scope", scope)
                .build();
        return Jwts.builder()
                .setClaims(claims.getClaims())
                .signWith(signingKey, SignatureAlgorithm.HS256)
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
        UserDetails userDetails = new UserDetailsImpl(user);
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

    /**
     * Check if a token is valid
     *
     * @param token The jwtoken
     * @return True if the token is non-expired and valid
     */
    public boolean validateToken(String token) {
        return getExpirationDate(token).after(new Date());
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Authentication getAuthentication(String token) {
        String email = getEmailFromToken(token);
        User user = this.userService.getUserByEmail(email);
        UserDetails userDetails = new UserDetailsImpl(user);

        return new UserAuthentication(userDetails);
    }

    /**
     * Get the currently logged-in user
     *
     * @return The currently logged-in user
     */
    public User getLoggedInUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;
        String email = auth.getName();
        return this.userService.getUserByEmail(email);
    }
}
