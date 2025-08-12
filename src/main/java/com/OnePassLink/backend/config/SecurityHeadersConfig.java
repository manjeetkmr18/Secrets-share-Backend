package com.OnePassLink.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

@Configuration
@EnableWebSecurity
@Profile("!dev")  // Only active when NOT in dev profile
public class SecurityHeadersConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/**").permitAll()
                .requestMatchers("/actuator/health", "/actuator/prometheus").permitAll()
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                .requestMatchers("/swagger-resources/**", "/webjars/**").permitAll()
                .anyRequest().denyAll()
            )
            .csrf(csrf -> csrf.disable()) // REST API, using proper CORS instead
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.deny()) // X-Frame-Options: DENY
                .contentTypeOptions(contentTypeOptions -> {}) // X-Content-Type-Options: nosniff
                .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                    .maxAgeInSeconds(31536000) // 1 year
                    .includeSubDomains(true)
                    .preload(true)
                )
                .referrerPolicy(referrerPolicy ->
                    referrerPolicy.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                )
                .addHeaderWriter((request, response) -> {
                    String requestPath = request.getRequestURI();

                    // Relaxed CSP for Swagger UI pages
                    if (requestPath.startsWith("/swagger-ui") || requestPath.startsWith("/v3/api-docs")) {
                        response.setHeader("Content-Security-Policy",
                            "default-src 'self'; " +
                            "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
                            "style-src 'self' 'unsafe-inline'; " +
                            "img-src 'self' data: blob:; " +
                            "connect-src 'self'; " +
                            "font-src 'self'; " +
                            "object-src 'none'; " +
                            "media-src 'none'; " +
                            "frame-src 'none'; " +
                            "base-uri 'self'; " +
                            "form-action 'self';"
                        );
                    } else {
                        // Strict CSP for API endpoints
                        response.setHeader("Content-Security-Policy",
                            "default-src 'self'; " +
                            "script-src 'self'; " +
                            "style-src 'self' 'unsafe-inline'; " +
                            "img-src 'self' data:; " +
                            "connect-src 'self'; " +
                            "font-src 'self'; " +
                            "object-src 'none'; " +
                            "media-src 'none'; " +
                            "frame-src 'none'; " +
                            "base-uri 'self'; " +
                            "form-action 'self';"
                        );
                    }

                    // Additional security headers
                    response.setHeader("X-Permitted-Cross-Domain-Policies", "none");
                    response.setHeader("Cross-Origin-Embedder-Policy", "require-corp");
                    response.setHeader("Cross-Origin-Opener-Policy", "same-origin");
                    response.setHeader("Cross-Origin-Resource-Policy", "same-origin");
                })
            );

        return http.build();
    }
}
