package ch.puzzle.eft.config;

import java.security.Principal;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class AuthenticationUser implements Authentication {
    private Principal principal;
    private String matriculationNumber;
    private String surname;
    private String givenName;
    private boolean authenticated;

    public AuthenticationUser(Principal principal, String matriculationNumber, String surname, String givenName) {
        this.principal = principal;
        this.matriculationNumber = matriculationNumber;
        this.surname = surname;
        this.givenName = givenName;
        this.authenticated = principal != null;
    }

    //This constructor is only used for a mock user in the ShibbolethRequestAuthenticationFilter
    public AuthenticationUser(String matriculationNumber, String name) {
        this(null, matriculationNumber, name, name);
        this.authenticated = true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal != null ? principal : surname;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return principal != null ? principal.getName() : surname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof AuthenticationUser that))
            return false;

        return new EqualsBuilder().append(authenticated, that.authenticated)
                                  .append(principal, that.principal)
                                  .append(matriculationNumber, that.matriculationNumber)
                                  .append(surname, that.surname)
                                  .append(givenName, that.givenName)
                                  .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(principal)
                                          .append(matriculationNumber)
                                          .append(surname)
                                          .append(givenName)
                                          .append(authenticated)
                                          .toHashCode();
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("AuthenticationUser{");
        sb.append("principal=")
          .append(principal);
        sb.append(", matriculationNumber='")
          .append(getMatriculationNumber())
          .append('\'');
        sb.append(", surname='")
          .append(getSurname())
          .append('\'');
        sb.append(", givenName='")
          .append(getGivenName())
          .append('\'');
        sb.append(", authenticated=")
          .append(isAuthenticated());
        sb.append('}');
        return sb.toString();
    }

    public String getMatriculationNumber() {
        return matriculationNumber;
    }

    public String getSurname() {
        return surname;
    }

    public String getGivenName() {
        return givenName;
    }
}
