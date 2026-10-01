package com.codeconnect.user.infrastructure.security;

import com.codeconnect.user.infrastructure.config.properties.InternalSecurityProperties;
import com.codeconnect.user.infrastructure.config.properties.SecurityPathProperties;
import com.codeconnect.user.infrastructure.security.filter.InternalAuthenticationFilter;
import com.codeconnect.user.infrastructure.security.handler.JsonAccessDeniedHandler;
import com.codeconnect.user.infrastructure.security.handler.JsonAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 6 / Spring Boot 3.3 {@link SecurityFilterChain} configuration for user-service.
 *
 * <h3>Filter Chain Position</h3>
 * {@link InternalAuthenticationFilter} is positioned <em>before</em>
 * {@link UsernamePasswordAuthenticationFilter} — the canonical position for stateless
 * header-based pre-authentication filters in Spring Security 6. This ensures:
 * <ol>
 *   <li>The {@code SecurityContextHolder} is populated before any authorization decision.</li>
 *   <li>Requests with valid internal headers are never treated as anonymous.</li>
 * </ol>
 *
 * <h3>Exception Handling</h3>
 * Because {@link InternalAuthenticationFilter} precedes {@code ExceptionTranslationFilter},
 * authentication failures are caught inside the filter and delegated directly to
 * {@link JsonAuthenticationEntryPoint} — producing a structured JSON 401 response.
 * Authorization failures (403) are handled by {@link JsonAccessDeniedHandler} via
 * the standard {@code ExceptionTranslationFilter} → {@code AccessDeniedHandler} path.
 *
 * <h3>Path Externalization</h3>
 * All HTTP security path patterns are injected via {@link SecurityPathProperties}
 * (bound to {@code codeconnect.security.paths.*}) — never hardcoded in Java source.
 */
@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@EnableConfigurationProperties({InternalSecurityProperties.class, SecurityPathProperties.class})
public class SecurityConfig {

    private final InternalAuthenticationFilter  internalAuthenticationFilter;
    private final JsonAuthenticationEntryPoint  authenticationEntryPoint;
    private final JsonAccessDeniedHandler       accessDeniedHandler;
    private final SecurityPathProperties        securityPathProperties;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(securityPathProperties.actuatorHealth()).permitAll()
                .requestMatchers(securityPathProperties.actuatorInfo()).permitAll()
                .requestMatchers(securityPathProperties.internalUsers()).permitAll()
                .requestMatchers(securityPathProperties.adminBase()).hasAuthority("ROLE_ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(internalAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
