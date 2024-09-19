package ch.puzzle.eft;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.saml2.provider.service.authentication.OpenSaml4AuthenticationProvider;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.security.web.SecurityFilterChain;

import java.security.Security;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        OpenSaml4AuthenticationProvider authenticationProvider = new OpenSaml4AuthenticationProvider();
        authenticationProvider.setResponseAuthenticationConverter(groupsConverter());


        http.authorizeHttpRequests(authorize -> authorize.requestMatchers("/home")
                                                         .authenticated()
                                                         .requestMatchers("/**")
                                                         .permitAll())
            // .saml2Login(saml2 -> saml2.authenticationManager(new ProviderManager(authenticationProvider)))
            .saml2Login(withDefaults())
            .saml2Logout(withDefaults());

        return http.build();
    }

    private Converter<OpenSaml4AuthenticationProvider.ResponseToken, Saml2Authentication> groupsConverter() {
        Converter<OpenSaml4AuthenticationProvider.ResponseToken, Saml2Authentication> delegate = OpenSaml4AuthenticationProvider.createDefaultResponseAuthenticationConverter();

        return (responseToken) -> {
            log.info("*".repeat(20));
            Saml2Authentication authentication = delegate.convert(responseToken);
            Saml2AuthenticatedPrincipal principal = (Saml2AuthenticatedPrincipal) authentication.getPrincipal();
            principal.getAttributes()
                     .forEach(log::info);
            authentication.getAuthorities()
                          .forEach(a -> log.info(a.getAuthority()));
            log.info("SAML2 response: {}", authentication.getSaml2Response());
            List<String> groups = principal.getAttribute("groups");
            Set<GrantedAuthority> authorities = new HashSet<>();
            if (groups != null) {
                log.info("Groups found, adding {} simple granted authorities", groups.size());
                groups.stream()
                      .map(SimpleGrantedAuthority::new)
                      .forEach(authorities::add);
            } else {
                log.info("No groups found, adding default authorities {}", authentication.getAuthorities());
                authorities.addAll(authentication.getAuthorities());
            }
            log.info("*".repeat(20));
            return new Saml2Authentication(principal, authentication.getSaml2Response(), authorities);
        };
    }
}
