package ch.puzzle.eft.config;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AuthenticationUserDetailsTest {

    @Test
    void shouldReturnCorrectDefaultValue() {
        AuthenticationUserDetails details = new AuthenticationUserDetails(new AuthenticationUser("", ""));

        assertEquals("n/a", details.getUsername());
        assertNull(details.getPassword());
        assertEquals(List.of(), details.getAuthorities());
    }
}
