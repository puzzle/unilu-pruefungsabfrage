package ch.puzzle.eft.service;

import ch.puzzle.eft.config.AuthenticationUser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    public String getMatriculationNumber() {
        AuthenticationUser authenticationUser = getAuthenticationUser();
        if (authenticationUser != null) {
            return authenticationUser.getMatriculationNumber();
        }
        return null;
    }

    public AuthenticationUser getAuthenticationUser() {
        Authentication authentication = SecurityContextHolder.getContext()
                                                             .getAuthentication();

        if (authentication.getPrincipal() instanceof AuthenticationUser authenticationUser) {
            logger.debug("authentication user found in security context is {}", authenticationUser);
            return authenticationUser;
        }
        logger.debug("no authentication user found in security context");
        return null;
    }
}
