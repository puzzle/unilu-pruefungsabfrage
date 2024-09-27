package ch.puzzle.eft.config;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.preauth.RequestAttributeAuthenticationFilter;
import org.springframework.security.web.header.writers.*;

import java.security.Security;
import java.util.Collection;
import java.util.List;

import static org.springframework.security.web.header.writers.CrossOriginEmbedderPolicyHeaderWriter.CrossOriginEmbedderPolicy.REQUIRE_CORP;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
    private static final String CSP_CONFIG = "default-src 'self';" + "script-src 'strict-dynamic' 'sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz';" + "style-src 'self';" + "img-src 'self'; " + "font-src 'self';" + "object-src 'none';" + "base-uri 'self';" + "form-action 'self';" + "frame-ancestors 'none'; ";
    private static final String PERMISSION_POLICY = "accelerometer=()," + "ambient-light-sensor=()," + "autoplay=(self <origin>)," + "battery=()," + "camera=()," + "display-capture=()," + "document-domain=()," + "encrypted-media=()," + "fullscreen=()," + "gamepad=()," + "geolocation=()," + "gyroscope=()," + "magnetometer=()," + "microphone=()," + "picture-in-picture=()," + "speaker-selection=()," + "usb=()," + "screen-wake-lock=()," + "xr-spatial-tracking=()";

    @Bean
    public AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> userDetailsService() {
        return token -> new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return List.of();
            }

            @Override
            public String getPassword() {
                String password = getMaskedPassword(token);
                logger.debug("current password is {}", password);
                return password;
            }

            @Override
            public String getUsername() {
                String username = token.getPrincipal() != null
                        ? token.getPrincipal()
                               .toString()
                        : "n/a";
                logger.debug("current username is {}", username);
                return username;
            }
        };
    }

    private static String getMaskedPassword(PreAuthenticatedAuthenticationToken token) {
        if (token.getCredentials() == null) {
            return "n/a";
        } else {
            String password = token.getCredentials()
                                   .toString();
            return password.charAt(0) + "*****" + password.charAt(password.length() - 1);
        }
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> userDetailsService) {
        PreAuthenticatedAuthenticationProvider authenticationProvider = new PreAuthenticatedAuthenticationProvider();
        authenticationProvider.setPreAuthenticatedUserDetailsService(userDetailsService);
        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public RequestAttributeAuthenticationFilter authenticationFilter(AuthenticationManager authenticationManager) {
        RequestAttributeAuthenticationFilter authenticationFilter = new ShibbolthRequestAuthenticationFilter();
        authenticationFilter.setPrincipalEnvironmentVariable("remoteUser");
        authenticationFilter.setAuthenticationManager(authenticationManager);
        authenticationFilter.setContinueFilterChainOnUnsuccessfulAuthentication(true);
        authenticationFilter.setCheckForPrincipalChanges(false);
        return authenticationFilter;
    }

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http, AuthenticationManager authenticationManager, RequestAttributeAuthenticationFilter authenticationFilter) throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        http.authorizeHttpRequests(authorize -> authorize.requestMatchers("/eft/unauthorized") // could be accessed without authentication
                                                         .permitAll()
                                                         .anyRequest()
                                                         .authenticated())
            .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .authenticationManager(authenticationManager)
            .headers(h -> h.frameOptions(HeadersConfigurer.FrameOptionsConfig::deny) // or frameOptions.deny()
                           .xssProtection(e -> e.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
                           .crossOriginEmbedderPolicy(coep -> coep.policy(REQUIRE_CORP))
                           .crossOriginOpenerPolicy(coopCustomizer -> coopCustomizer.policy(CrossOriginOpenerPolicyHeaderWriter.CrossOriginOpenerPolicy.SAME_ORIGIN))
                           .crossOriginResourcePolicy(corpCustomizer -> corpCustomizer.policy(CrossOriginResourcePolicyHeaderWriter.CrossOriginResourcePolicy.SAME_ORIGIN))
                           .addHeaderWriter(new StaticHeadersWriter("X-Permitted-Cross-Domain-Policies", "none"))
                           .referrerPolicy(referrer -> referrer.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                           .httpStrictTransportSecurity(hsts -> hsts.maxAgeInSeconds(31536000)
                                                                    .includeSubDomains(true))
                           .addHeaderWriter((request, response) -> response.setHeader("Cross-Origin-Embedder-Policy",
                                                                                      "require-corp"))
                           .addHeaderWriter((request, response) -> response.setHeader("Cross-Origin-Resource-Policy",
                                                                                      "same-site"))
                           .addHeaderWriter((request, response) -> response.setHeader("Cross-Origin-Opener-Policy",
                                                                                      "same-origin"))
                           .addHeaderWriter((request, response) -> response.setHeader("X-Permitted-Cross-Domain-Policies",
                                                                                      "none"))
                           .addHeaderWriter((request, response) -> response.setHeader("Server", ""))
                           .contentSecurityPolicy(csp -> csp.policyDirectives(CSP_CONFIG))
                           .permissionsPolicy(pp -> pp.policy(PERMISSION_POLICY)))
            .securityContext((securityContext) -> securityContext.requireExplicitSave(true))
            .sessionManagement((sessions) -> sessions.requireExplicitAuthenticationStrategy(true));

        return http.build();
    }
}
