package ch.puzzle.eft;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.ContentTypeOptionsConfig;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
        return http.cors(Customizer.withDefaults())
                   .authorizeHttpRequests(e -> e.anyRequest()
                                                .permitAll())
                   .headers(h -> h.frameOptions(FrameOptionsConfig::sameOrigin) // or frameOptions.deny()

                                  // X-Content-Type-Options: nosniff
                                  .contentTypeOptions(ContentTypeOptionsConfig::disable)

                                  // Referrer-Policy: strict-origin-when-cross-origin
                                  .referrerPolicy(referrer -> referrer.policy(ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))

                                  // Strict-Transport-Security: max-age=31536000; includeSubDomains
                                  .httpStrictTransportSecurity(hsts -> hsts.maxAgeInSeconds(31536000)
                                                                           .includeSubDomains(true))

                                  // Cross-Origin-Embedder-Policy: require-corp
                                  .addHeaderWriter((request, response) -> response.setHeader("Cross-Origin-Embedder-Policy",
                                                                                             "require-corp"))

                                  // Cross-Origin-Resource-Policy: same-site (or same-origin)
                                  .addHeaderWriter((request, response) -> response.setHeader("Cross-Origin-Resource-Policy",
                                                                                             "same-site"))

                                  // Cross-Origin-Opener-Policy: same-origin
                                  .addHeaderWriter((request, response) -> response.setHeader("Cross-Origin-Opener-Policy",
                                                                                             "same-origin"))

                                  // X-Permitted-Cross-Domain-Policies: none
                                  .addHeaderWriter((request, response) -> response.setHeader("X-Permitted-Cross-Domain-Policies",
                                                                                             "none"))
                                  .addHeaderWriter((request, response) -> response.setHeader("Server", ""))

                                  // Content-Security-Policy: Customize according to your needs (e.g., using Nonces or Hashes)
                                  .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'; " + "script-src 'self'; " + "style-src 'self'" + "img-src 'self' data:; " + "font-src 'self'; " + "object-src 'none'; " + "base-uri 'self'; " + "form-action 'self'; " + "frame-ancestors 'none'; " + "upgrade-insecure-requests;"))

                                  // Permissions-Policy: Customize according to your needs
                                  .permissionsPolicy(pp -> pp.policy("accelerometer=()," + "ambient-light-sensor=()," + "autoplay=(self <origin>)," + "battery=()," + "camera=()," + "display-capture=()," + "document-domain=()," + "encrypted-media=()," + "fullscreen=()," + "gamepad=()," + "geolocation=()," + "gyroscope=()," + "magnetometer=()," + "microphone=()," + "picture-in-picture=()," + "speaker-selection=()," + "usb=()," + "screen-wake-lock=()," + "xr-spatial-tracking=()") // Add your Permissions-Policy here
                                  ))
                   .build();
    }
}