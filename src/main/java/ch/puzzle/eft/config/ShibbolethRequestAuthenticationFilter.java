package ch.puzzle.eft.config;

import java.io.UnsupportedEncodingException;
import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.authentication.preauth.RequestAttributeAuthenticationFilter;

import jakarta.servlet.http.HttpServletRequest;

public class ShibbolethRequestAuthenticationFilter extends RequestAttributeAuthenticationFilter {
    private static final Logger logger = LoggerFactory.getLogger(ShibbolethRequestAuthenticationFilter.class);
    private static final String ATTRIBUTE_MATRICULATION_NUMBER = "matriculationNumber";
    private static final String ATTRIBUTE_SURNAME = "surname";
    private static final String ATTRIBUTE_GIVEN_NAME = "givenName";
    @Value("${server.unauthorized.urls:/}")
    private String unauthorizedUrls;
    @Value("${test.mock.principal:false}")
    private boolean mockPrincipal;


    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        fixStringAttribute(request, ATTRIBUTE_SURNAME);
        fixStringAttribute(request, ATTRIBUTE_GIVEN_NAME);
        AuthenticationUser authenticationUser = createAuthenticationUser(request);
        if (authenticationUser == null) {
            logger.debug("no principal, fallback to configured principal environment variable");
            return super.getPreAuthenticatedPrincipal(request);
        }
        logger.debug("authentication user from request is {}", authenticationUser);
        return authenticationUser;
    }

    private AuthenticationUser createAuthenticationUser(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal == null || principal.getName() == null) {
            if (checkPrincipal(request)) {
                logger.debug("no principal for unauthorized page required, returning anonymous principal");
                if (mockPrincipal) {
                    return new AuthenticationUser("11112222", "mock");
                }
                return new AuthenticationUser("anonymous");
            }
            return null;
        }
        return new AuthenticationUser(principal,
                                      getStringAttribute(request, ATTRIBUTE_MATRICULATION_NUMBER),
                                      getStringAttribute(request, ATTRIBUTE_SURNAME),
                                      getStringAttribute(request, ATTRIBUTE_GIVEN_NAME));
    }

    private String getStringAttribute(HttpServletRequest request, String attributeName) {
        if (request.getAttribute(attributeName) != null) {
            try {
                return (String) request.getAttribute(attributeName);
            } catch (ClassCastException | NullPointerException e) {
                logger.info("unable to get string attribute {}: {}", attributeName, e);
            }
        }
        return null;
    }

    private boolean checkPrincipal(HttpServletRequest request) {
        return unauthorizedUrls.contains(request.getRequestURI()) || mockPrincipal;
    }

    /**
     * Fix string attributes from ISO-8859-1 to UTF-8.
     *
     * @see <a href="https://shibboleth.atlassian.net/wiki/spaces/SP3/pages/2065335257/AttributeAccess">Attribute
     *      Access</a>
     */
    private void fixStringAttribute(HttpServletRequest request, String attributeName) {
        if (request.getAttribute(attributeName) != null) {
            try {
                String value = (String) request.getAttribute(attributeName);
                value = new String(value.getBytes("ISO-8859-1"), "UTF-8");
                request.setAttribute(attributeName, value);
                logger.debug("converted string attribute '{}' from ISO-8859-1 to UTF-8 is '{}'", attributeName, value);
            } catch (UnsupportedEncodingException | ClassCastException | NullPointerException e) {
                logger.info("unable to convert string attribute {} to UTF-8: {}", attributeName, e);
            }
        }
    }
}