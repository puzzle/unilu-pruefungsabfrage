package ch.puzzle.eft.config;

import java.security.Principal;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.preauth.RequestAttributeAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.header.writers.*;

import static org.springframework.security.web.header.writers.CrossOriginEmbedderPolicyHeaderWriter.CrossOriginEmbedderPolicy.REQUIRE_CORP;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static final String CSP_CONFIG = "base-uri 'self';" + "connect-src 'none';" + "default-src 'self';" + "script-src 'strict-dynamic' 'sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz';" + "style-src 'self';" + "img-src 'self' data:; " + "font-src 'self';" + "object-src 'none';" + "form-action 'self';" + "frame-ancestors 'none';" + "frame-src 'none';";
    private static final String PERMISSION_POLICY = "accelerometer=()," + "attribution-reporting=()," + "autoplay=()," + "camera=()," + "compute-pressure=()," + "display-capture=()," + "encrypted-media=()," + "fullscreen=()," + "gamepad=()," + "geolocation=()," + "gyroscope=()," + "hid=()," + "identity-credentials-get=()," + "idle-detection=()," + "local-fonts=()," + "magnetometer=()," + "microphone=()," + "midi=()," + "otp-credentials=()," + "payment=()," + "picture-in-picture=()," + "publickey-credentials-create=()," + "publickey-credentials-get=()," + "screen-wake-lock=()," + "serial=()," + "storage-access=()," + "usb=()," + "window-management=()," + "xr-spatial-tracking=()";

    @Value("${test.mock.isMockPrincipal:false}")
    private boolean isMockPrincipal;

    @Bean
    public AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> userDetailsService() {
        return token -> new AuthenticationUserDetails((AuthenticationUser) token.getPrincipal()) {
            @Override
            public String getUsername() {
                String username = "n/a";
                if (token.getPrincipal() instanceof Principal principal) {
                    username = principal.getName();
                }
                return username;
            }
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> userDetailsService) {
        PreAuthenticatedAuthenticationProvider authenticationProvider = new PreAuthenticatedAuthenticationProvider();
        authenticationProvider.setPreAuthenticatedUserDetailsService(userDetailsService);
        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public RequestAttributeAuthenticationFilter authenticationFilter(AuthenticationManager authenticationManager) {
        RequestAttributeAuthenticationFilter authenticationFilter = new ShibbolethRequestAuthenticationFilter(isMockPrincipal);
        authenticationFilter.setAuthenticationManager(authenticationManager);
        // Stop infinity loop if login not successful
        authenticationFilter.setContinueFilterChainOnUnsuccessfulAuthentication(false);
        authenticationFilter.setCheckForPrincipalChanges(false);
        return authenticationFilter;
    }

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http, AuthenticationManager authenticationManager, RequestAttributeAuthenticationFilter authenticationFilter) throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        http.cors(Customizer.withDefaults())
            .authorizeHttpRequests(authorize -> authorize.requestMatchers("/**") // pre-authenticated by Shibboleth
                                                         .authenticated()
                                                         .anyRequest()
                                                         .denyAll())
            .addFilterBefore(authenticationFilter, SecurityContextHolderFilter.class)
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
