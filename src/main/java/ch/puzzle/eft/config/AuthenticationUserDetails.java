package ch.puzzle.eft.config;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class AuthenticationUserDetails extends AuthenticationUser implements UserDetails {

    public AuthenticationUserDetails(AuthenticationUser authenticationUser) {
        super(authenticationUser,
              authenticationUser.getMatriculationNumber(),
              authenticationUser.getSurname(),
              authenticationUser.getGivenName());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    /**
     * THIS MUST ALWAYS RETURN NULL. This override is enforced by the UserDetails interface.
     * 
     * @return null
     */
    @Override
    public String getPassword() {
        return null;
    }

    /**
     * This must never return null but must always return "n/a"
     * 
     * @return "n/a"
     */
    @Override
    public String getUsername() {
        return "n/a";
    }
}
