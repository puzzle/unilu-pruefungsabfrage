package ch.puzzle.eft.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthenticationUserDetailsTest {

    @Test
    void shouldReturnStringNotNull() {
        AuthenticationUserDetails details = new AuthenticationUserDetails(new AuthenticationUser("", ""));

        String result = details.getUsername();

        assertEquals("n/a", result);
    }
}
