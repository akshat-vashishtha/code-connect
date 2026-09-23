package com.codeconnect.gateway.application.validator;

import com.codeconnect.gateway.application.dto.request.LoginRequest;
import com.codeconnect.gateway.domain.exception.AccountBannedException;
import com.codeconnect.gateway.domain.exception.InvalidCredentialsException;
import com.codeconnect.gateway.domain.model.User;
import com.codeconnect.gateway.domain.model.UserStatus;
import com.codeconnect.gateway.domain.repository.ReactiveUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Domain validator ensuring authentication credentials and account state invariants.
 * Prevents user enumeration by producing identical InvalidCredentialsException
 * regardless of whether the email is unknown or the password is incorrect.
 */
@Slf4j
@Component
public class CredentialValidator {

    private final ReactiveUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CredentialValidator(ReactiveUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Mono<User> validateCredentials(LoginRequest request, String normalizedEmail) {
        return userRepository.findByEmail(normalizedEmail)
            .switchIfEmpty(Mono.defer(() -> {
                log.warn("Authentication failed: email not found for email={}", normalizedEmail);
                return Mono.error(new InvalidCredentialsException());
            }))
            .flatMap(user -> {
                if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
                    log.warn("Authentication failed: password mismatch for userId={}", user.getId());
                    return Mono.error(new InvalidCredentialsException());
                }

                if (user.getStatus() == UserStatus.BANNED) {
                    log.warn("Authentication rejected: account is banned for userId={}", user.getId());
                    return Mono.error(new AccountBannedException());
                }

                return Mono.just(user);
            });
    }
}
