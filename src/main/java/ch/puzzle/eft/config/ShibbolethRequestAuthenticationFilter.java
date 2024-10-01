package ch.puzzle.eft.config;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.authentication.preauth.RequestAttributeAuthenticationFilter;

import java.io.UnsupportedEncodingException;
import java.security.Principal;

public class ShibbolethRequestAuthenticationFilter extends RequestAttributeAuthenticationFilter {
    private static final Logger logger = LoggerFactory.getLogger(ShibbolethRequestAuthenticationFilter.class);
    @Value("${server.home.url:/home.html}")
    private String homeUrl;


    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        fixStringAttribute(request, "surname");
        fixStringAttribute(request, "givenName");
        Principal principal = request.getUserPrincipal();
        if (principal == null || principal.getName() == null) {
            if (homeUrl.equals(request.getRequestURI())) {
                logger.debug("no principal and home page requested, returning anonymous principal");
                return "anonymous principal for home page";
            }
            logger.debug("no principal, fallback to configured principal environment variable");
            return super.getPreAuthenticatedPrincipal(request);
        }
        logger.debug("user principal from request is {}", principal);
        return principal;
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
                request.setAttribute(attributeName, value + "(äöü)");
                logger.debug("converted string attribute '{}' from ISO-8859-1 to UTF-8 is '{}'", attributeName, value);
            } catch (UnsupportedEncodingException | ClassCastException | NullPointerException e) {
                logger.info("unable to convert string attribute {} to UTF-8: {}", attributeName, e);
            }
        }
    }
}
