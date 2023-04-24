package no.freshify.api.security;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCookieFactory {
    @Test
    public void testGetAuthorizationCookie() {
        String token = "helloworld";
        Cookie cookie = CookieFactory.getAuthorizationCookie(token);

        assertEquals("Authorization", cookie.getName());
        assertEquals(token, cookie.getValue());
    }

    @Test
    public void testGetClearCookie() {
        Cookie cookie = CookieFactory.getClearCookie();

        assertEquals("Authorization", cookie.getName());
        assertEquals("", cookie.getValue());
    }
}
