package ch.puzzle.eft;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.CrossOriginOpenerPolicyHeaderWriter.CrossOriginOpenerPolicy;
import org.springframework.security.web.header.writers.CrossOriginResourcePolicyHeaderWriter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

import static org.springframework.security.web.header.writers.CrossOriginEmbedderPolicyHeaderWriter.CrossOriginEmbedderPolicy.REQUIRE_CORP;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static final String CSP_CONFIG = "base-uri 'self';" + "connect-src 'none';" + "default-src 'self';" + "script-src 'strict-dynamic' 'sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz';" + "style-src 'self';" + "img-src 'self' data:; " + "font-src 'self';" + "object-src 'none';" + "form-action 'self';" + "frame-ancestors 'none'; " + "frame-src 'none';"; //TODO: restrict upgrade-insecure-requests
    private static final String PERMISSION_POLICY = "accelerometer=()," + "attribution-reporting=()," + "autoplay=()," + "camera=()," + "compute-pressure=()," + "display-capture=()," + "encrypted-media=()," + "fullscreen=()," + "gamepad=()," + "geolocation=()," + "gyroscope=()," + "hid=()," + "identity-credentials-get=()," + "idle-detection=()," + "local-fonts=()," + "magnetometer=()," + "microphone=()," + "midi=()," + "otp-credentials=()," + "payment=()," + "picture-in-picture=()," + "publickey-credentials-create=()," + "publickey-credentials-get=()," + "screen-wake-lock=()," + "serial=()," + "storage-access=()," + "usb=()," + "window-management=()," + "xr-spatial-tracking=()";

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
        return http.cors(Customizer.withDefaults())
                   .authorizeHttpRequests(e -> e.anyRequest()
                                                .permitAll())
                   .headers(h -> h.frameOptions(FrameOptionsConfig::deny) // or frameOptions.deny()
                                  .xssProtection(e -> e.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
                                  .crossOriginEmbedderPolicy(coep -> coep.policy(REQUIRE_CORP))
                                  .crossOriginOpenerPolicy(coopCustomizer -> coopCustomizer.policy(CrossOriginOpenerPolicy.SAME_ORIGIN))
                                  .crossOriginResourcePolicy(corpCustomizer -> corpCustomizer.policy(CrossOriginResourcePolicyHeaderWriter.CrossOriginResourcePolicy.SAME_ORIGIN))
                                  .addHeaderWriter(new StaticHeadersWriter("X-Permitted-Cross-Domain-Policies", "none"))
                                  .referrerPolicy(referrer -> referrer.policy(ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
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
                   .build();
    }
}