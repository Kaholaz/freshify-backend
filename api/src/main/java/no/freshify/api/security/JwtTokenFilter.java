package no.freshify.api.security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.SignatureException;
import java.util.Arrays;

/**
 * Filter for checking if the user is authenticated
 */
@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    private final AuthenticationService authenticationService;

    JwtTokenFilter(@Lazy AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * Filter for checking if the user is authenticated
     *
     * @param request     The request
     * @param response    The response
     * @param filterChain The filter chain
     * @throws ServletException If the filter fails
     * @throws IOException      If the filter fails
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        // Allow users logout without a token
        if (request.getRequestURI().endsWith("/user/logout")) {
            filterChain.doFilter(request, response);
            return;
        }

        Cookie[] cookies = request.getCookies();
        try {
            if (cookies != null && cookies.length != 0) {
                Arrays.stream(cookies)
                        .filter(cookie -> cookie.getName().equals("Authorization"))
                        .findFirst().ifPresent(cookie -> {
                            SecurityContextHolder.getContext()
                                    .setAuthentication(authenticationService.getAuthentication(cookie.getValue()));
                            response.addCookie(
                                    CookieFactory.getAuthorizationCookie(
                                            authenticationService.renewToken(cookie.getValue())
                                    )
                            );
                        });
            }
        } catch (ExpiredJwtException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Invalid token");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
