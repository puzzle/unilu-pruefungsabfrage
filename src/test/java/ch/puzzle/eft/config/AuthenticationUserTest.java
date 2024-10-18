package ch.puzzle.eft.config;

import java.security.Principal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthenticationUserTest {

    @Test
    void shouldReturnSurnameWhenPrincipalIsNull() {
        AuthenticationUser user = new AuthenticationUser(null, "", "Surname", "");

        Object result = user.getPrincipal();

        assertInstanceOf(String.class, result);
        assertEquals("Surname", result);
    }

    @Test
    void shouldReturnPrincipal() {
        Principal principal = mock(Principal.class);
        AuthenticationUser user = new AuthenticationUser(principal, "", "", "");

        Object result = user.getPrincipal();

        assertInstanceOf(Principal.class, result);
        assertEquals(principal, result);
    }

    @Test
    void shouldSetAuthenticatedToTrueWhenPrincipalIsSupplied() {
        Principal principal = mock(Principal.class);

        AuthenticationUser result = new AuthenticationUser(principal, "", "", "");

        assertTrue(result.isAuthenticated());
    }

    @Test
    void shouldSetAuthenticatedToFalseWhenPrincipalIsNull() {
        AuthenticationUser result = new AuthenticationUser(null, "", "", "");

        assertFalse(result.isAuthenticated());
    }

    @Test
    void shouldSetAuthenticatedToTrue() {
        AuthenticationUser result = new AuthenticationUser("", "");

        assertTrue(result.isAuthenticated());
    }

    @Test
    void ShouldReturnNameFromPrincipal() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("Principal Name");

        AuthenticationUser user = new AuthenticationUser(principal, "", "", "");

        assertEquals("Principal Name", user.getName());
    }

    @Test
    void shouldReturnSurnameWhenPrincipalIsNull2() {
        AuthenticationUser user = new AuthenticationUser(null, "", "Surnames", "");

        assertEquals("Surnames", user.getName());
    }

    @Test
    void shouldBeEqualWhenUserIsTheSame() {
        AuthenticationUser user = new AuthenticationUser(null, "123", "Surname", "GivenName");

        boolean result = user.equals(user);

        assertTrue(result);
    }

    @ParameterizedTest
    @ValueSource(classes = {Object.class, String.class, Boolean.class})
    void shouldNotBeEqualWhenObjectIsAnotherClass(Object clazz) {
        AuthenticationUser user = new AuthenticationUser(null, "123", "Surname", "GivenName");

        boolean result = user.equals(clazz);

        assertFalse(result);
    }

    @Test
    void shouldNotBeEqualWhenAuthenticatedIsNotEqual() {
        AuthenticationUser user1 = new AuthenticationUser(null, "123", "Surname", "GivenName");
        AuthenticationUser user2 = new AuthenticationUser(null, "123", "Surname", "GivenName");
        user1.setAuthenticated(true);
        user2.setAuthenticated(false);

        boolean result1 = user1.equals(user2);
        boolean result2 = user2.equals(user1);

        assertFalse(result1);
        assertFalse(result2);
    }

    @Test
    void shouldNotBeEqualWhenPrincipalIsNotEqual() {
        AuthenticationUser user1 = new AuthenticationUser(null, "123", "Surname", "GivenName");
        AuthenticationUser user2 = new AuthenticationUser(mock(Principal.class), "123", "Surname", "GivenName");

        boolean result1 = user1.equals(user2);
        boolean result2 = user2.equals(user1);

        assertFalse(result1);
        assertFalse(result2);
    }

    @Test
    void shouldNotBeEqualWhenMatriculationNumberIsNotEqual() {
        AuthenticationUser user1 = new AuthenticationUser(null, "123", "Surname", "GivenName");
        AuthenticationUser user2 = new AuthenticationUser(null, "321", "Surname", "GivenName");

        boolean result1 = user1.equals(user2);
        boolean result2 = user2.equals(user1);

        assertFalse(result1);
        assertFalse(result2);
    }

    @Test
    void shouldNotBeEqualWhenSurnameIsNotEqual() {
        AuthenticationUser user1 = new AuthenticationUser(null, "123", "Surname", "GivenName");
        AuthenticationUser user2 = new AuthenticationUser(null, "123", "emanrus", "GivenName");

        boolean result1 = user1.equals(user2);
        boolean result2 = user2.equals(user1);

        assertFalse(result1);
        assertFalse(result2);
    }

    @Test
    void shouldNotBeEqualWhenGivenNameIsNotEqual() {
        AuthenticationUser user1 = new AuthenticationUser(null, "123", "Surname", "GivenName");
        AuthenticationUser user2 = new AuthenticationUser(null, "123", "Surname", "emaNneviG");

        boolean result1 = user1.equals(user2);
        boolean result2 = user2.equals(user1);

        assertFalse(result1);
        assertFalse(result2);
    }

    @Test
    void shouldReturnNullForHardcodedValues() {
        AuthenticationUser authenticationUser = new AuthenticationUser(null, "123", "Surname", "GivenName");
        assertNull(authenticationUser.getCredentials());
        assertNull(authenticationUser.getDetails());
    }

    @Test
    void shouldBeEqualWhenUsersAreEqual() {
        AuthenticationUser user1 = new AuthenticationUser(null, "123", "Surname", "GivenName");
        AuthenticationUser user2 = new AuthenticationUser(null, "123", "Surname", "GivenName");

        boolean result1 = user1.equals(user2);
        boolean result2 = user2.equals(user1);

        assertTrue(result1);
        assertTrue(result2);
    }

}