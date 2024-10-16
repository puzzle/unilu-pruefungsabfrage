package ch.puzzle.eft.config;

import java.security.Principal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShibbolethRequestAuthenticationFilterTest {
    ShibbolethRequestAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new ShibbolethRequestAuthenticationFilter(false);
    }

    @Test
    void shouldReturnAuthenticationUserWhenPrincipalInRequest() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Principal principal = mock(Principal.class);
        when(request.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("NAME");
        when(request.getAttribute("matriculationNumber")).thenReturn("11112222");
        when(request.getAttribute("surname")).thenReturn("Sören");
        when(request.getAttribute("givenName")).thenReturn("Cédric");

        Object result = filter.getPreAuthenticatedPrincipal(request);

        assertInstanceOf(AuthenticationUser.class, result);
        AuthenticationUser authenticationUser = (AuthenticationUser) result;
        assertEquals("Sören", authenticationUser.getSurname());
        assertEquals("Cédric", authenticationUser.getGivenName());
        assertEquals("11112222", authenticationUser.getMatriculationNumber());
    }

    @Test
    void shouldReturnMockUserWhenMockPrincipalIsEnabled() {
        this.filter = new ShibbolethRequestAuthenticationFilter(true);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getUserPrincipal()).thenReturn(null);

        Object result = filter.getPreAuthenticatedPrincipal(request);

        assertInstanceOf(AuthenticationUser.class, result);
        AuthenticationUser authenticationUser = (AuthenticationUser) result;
        assertEquals("mock", authenticationUser.getSurname());
        assertEquals("mock", authenticationUser.getGivenName());
        assertEquals("11112222", authenticationUser.getMatriculationNumber());
        assertEquals("mock", authenticationUser.getPrincipal());
    }

    @Test
    void shouldReturnMockUserWhenMockPrincipalIsEnabledAndUserPrincipalHasNoName() {
        this.filter = new ShibbolethRequestAuthenticationFilter(true);
        HttpServletRequest request = mock(HttpServletRequest.class);
        Principal principal = mock(Principal.class);
        when(request.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn(null);
        when(request.getAttribute("surname")).thenReturn("Sören");
        when(request.getAttribute("givenName")).thenReturn("Cédric");

        Object result = filter.getPreAuthenticatedPrincipal(request);

        assertInstanceOf(AuthenticationUser.class, result);
        AuthenticationUser authenticationUser = (AuthenticationUser) result;
        assertEquals("mock", authenticationUser.getSurname());
        assertEquals("mock", authenticationUser.getGivenName());
        assertEquals("11112222", authenticationUser.getMatriculationNumber());
        assertEquals("mock", authenticationUser.getPrincipal());
    }
}