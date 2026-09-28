package com.codeconnect.curriculum.infrastructure.config;

import com.codeconnect.curriculum.infrastructure.security.InternalAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Spring Security configuration for curriculum-service.
 * Enforces zero-trust local SecurityFilterChain, stateless sessions, and role-based path protection.
 */
@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(InternalSecurityProperties.class)
@RequiredArgsConstructor
public class SecurityConfig {

    private final InternalAuthenticationFilter internalAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health/**", "/actuator/info").permitAll()
                .requestMatchers("/api/v1/admin/curriculum/**").hasAnyRole("ADMIN", "MENTOR")
                .requestMatchers("/api/v1/curriculum/**").authenticated()
                .anyRequest().authenticated()
            )
            .addFilterBefore(internalAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
