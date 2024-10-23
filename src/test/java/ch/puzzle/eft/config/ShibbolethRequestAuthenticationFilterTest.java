package ch.puzzle.eft.config;

import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
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

        Map<String, Object> requestAttributes = new HashMap<>();

        doAnswer(invocation -> {
            String name = invocation.getArgument(0);
            Object value = invocation.getArgument(1);
            requestAttributes.put(name, value);
            return null;
        }).when(request)
          .setAttribute(anyString(), any());

        doAnswer(invocation -> {
            String name = invocation.getArgument(0);
            return requestAttributes.get(name);
        }).when(request)
          .getAttribute(anyString());

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("NAME");

        when(request.getUserPrincipal()).thenReturn(principal);
        request.setAttribute("matriculationNumber", "11112222");
        request.setAttribute("surname", new String("Sören".getBytes(), StandardCharsets.ISO_8859_1));
        request.setAttribute("givenName", new String("Cédric".getBytes(), StandardCharsets.ISO_8859_1));

        Object result = filter.getPreAuthenticatedPrincipal(request);

        assertInstanceOf(AuthenticationUser.class, result);
        AuthenticationUser authenticationUser = (AuthenticationUser) result;
        assertEquals("Sören", authenticationUser.getSurname());
        assertEquals("Cédric", authenticationUser.getGivenName());
        assertEquals("11112222", authenticationUser.getMatriculationNumber());
    }

    @Test
    void shouldReturnMockUserWhenIsMockPrincipalIsTrue() {
        this.filter = new ShibbolethRequestAuthenticationFilter(true);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getUserPrincipal()).thenReturn(null);

        Object result = filter.getPreAuthenticatedPrincipal(request);

        assertInstanceOf(AuthenticationUser.class, result);
        AuthenticationUser authenticationUser = (AuthenticationUser) result;
        assertEquals("mock", authenticationUser.getSurname());
        assertEquals("mock", authenticationUser.getGivenName());
        assertEquals("01911506", authenticationUser.getMatriculationNumber());
        assertNull(authenticationUser.getPrincipal());
    }

    @Test
    void shouldReturnMockUserWhenIsMockPrincipalIsTrueAndUserPrincipalHasNoName() {
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
        assertEquals("01911506", authenticationUser.getMatriculationNumber());
        assertNull(authenticationUser.getPrincipal());
    }

    @Test
    void shouldSetPropertiesToNullWhenAttributesAreNotSet() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Principal principal = mock(Principal.class);
        when(request.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("Principal Name");

        Object result = filter.getPreAuthenticatedPrincipal(request);

        assertInstanceOf(AuthenticationUser.class, result);
        AuthenticationUser authenticationUser = (AuthenticationUser) result;
        assertNull(authenticationUser.getSurname());
        assertNull(authenticationUser.getGivenName());
        assertNull(authenticationUser.getMatriculationNumber());
    }

    @Test
    void shouldReturnPreAuthenticatedPrincipalWhenPrincipalIsNullAndIsMockPrincipalIsFalse() {
        this.filter = new ShibbolethRequestAuthenticationFilter(false);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getUserPrincipal()).thenReturn(null);
        doReturn("Remote User").when(request)
                               .getAttribute(any());

        Object result = filter.getPreAuthenticatedPrincipal(request);

        assertInstanceOf(String.class, result);
        assertEquals("Remote User", result);
    }
}