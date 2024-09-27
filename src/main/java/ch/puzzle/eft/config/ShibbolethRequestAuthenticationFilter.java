package ch.puzzle.eft.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.authentication.preauth.RequestAttributeAuthenticationFilter;

public class ShibbolethRequestAuthenticationFilter extends RequestAttributeAuthenticationFilter {

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        String principal = request.getRemoteUser();
        if (principal == null) {
            return super.getPreAuthenticatedPrincipal(request);
        }
        return principal;
    }
}
