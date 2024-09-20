package ch.puzzle.eft;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.ProviderManager;
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
    private static final String CSP_CONFIG = "default-src 'self';" + "script-src 'strict-dynamic' 'sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz';" + "style-src 'self';" + "img-src 'self'; " + "font-src 'self';" + "object-src 'none';" + "base-uri 'self';" + "form-action 'self';" + "frame-ancestors 'none'; ";
    private static final String PERMISSION_POLICY = "accelerometer=()," + "ambient-light-sensor=()," + "autoplay=(self <origin>)," + "battery=()," + "camera=()," + "display-capture=()," + "document-domain=()," + "encrypted-media=()," + "fullscreen=()," + "gamepad=()," + "geolocation=()," + "gyroscope=()," + "magnetometer=()," + "microphone=()," + "picture-in-picture=()," + "speaker-selection=()," + "usb=()," + "screen-wake-lock=()," + "xr-spatial-tracking=()";

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        OpenSaml4AuthenticationProvider authenticationProvider = new OpenSaml4AuthenticationProvider();
        authenticationProvider.setResponseAuthenticationConverter(groupsConverter());

        http.authorizeHttpRequests(authorize -> authorize.requestMatchers("/authorized")
                                                         .authenticated()
                                                         .requestMatchers("/**")
                                                         .permitAll())
            .saml2Login(saml2 -> saml2.authenticationManager(new ProviderManager(authenticationProvider)))
            // .saml2Login(withDefaults())
            .saml2Logout(withDefaults())
            //            .headers(h -> h.frameOptions(HeadersConfigurer.FrameOptionsConfig::deny) // or frameOptions.deny()
            //                           .xssProtection(e -> e.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
            //                           .crossOriginEmbedderPolicy(coep -> coep.policy(REQUIRE_CORP))
            //                           .crossOriginOpenerPolicy(coopCustomizer -> coopCustomizer.policy(CrossOriginOpenerPolicyHeaderWriter.CrossOriginOpenerPolicy.SAME_ORIGIN))
            //                           .crossOriginResourcePolicy(corpCustomizer -> corpCustomizer.policy(CrossOriginResourcePolicyHeaderWriter.CrossOriginResourcePolicy.SAME_ORIGIN))
            //                           .addHeaderWriter(new StaticHeadersWriter("X-Permitted-Cross-Domain-Policies", "none"))
            //                           .referrerPolicy(referrer -> referrer.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
            //                           .httpStrictTransportSecurity(hsts -> hsts.maxAgeInSeconds(31536000)
            //                                                                    .includeSubDomains(true))
            //                           .addHeaderWriter((request, response) -> response.setHeader("Cross-Origin-Embedder-Policy",
            //                                                                                      "require-corp"))
            //                           .addHeaderWriter((request, response) -> response.setHeader("Cross-Origin-Resource-Policy",
            //                                                                                      "same-site"))
            //                           .addHeaderWriter((request, response) -> response.setHeader("Cross-Origin-Opener-Policy",
            //                                                                                      "same-origin"))
            //                           .addHeaderWriter((request, response) -> response.setHeader("X-Permitted-Cross-Domain-Policies",
            //                                                                                      "none"))
            //                           .addHeaderWriter((request, response) -> response.setHeader("Server", ""))
            //                           .contentSecurityPolicy(csp -> csp.policyDirectives(CSP_CONFIG))
            //                           .permissionsPolicy(pp -> pp.policy(PERMISSION_POLICY)))
            .securityContext((securityContext) -> securityContext.requireExplicitSave(true))
            .sessionManagement((sessions) -> sessions.requireExplicitAuthenticationStrategy(true));

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
