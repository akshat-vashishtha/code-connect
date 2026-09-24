package com.codeconnect.user.application.service.collaborator.auth;

import com.codeconnect.user.application.dto.request.UserAuthenticationRequest;
import com.codeconnect.user.domain.exception.AccountBannedException;
import com.codeconnect.user.domain.exception.InvalidCredentialsException;
import com.codeconnect.user.domain.model.User;
import com.codeconnect.user.domain.repository.UserRepository;
import com.codeconnect.user.domain.valueobject.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Collaborator responsible for validating user credentials and account status during authentication.
 * Strictly adheres to Single Responsibility Principle (SRP).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserCredentialAuthenticator {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User authenticate(UserAuthenticationRequest request) {
        String normalizedEmail = new Email(request.email()).value();

        User user = userRepository.findByEmail(normalizedEmail)
            .orElseThrow(() -> {
                log.warn("Authentication failed: email not found for email={}", normalizedEmail);
                return new InvalidCredentialsException();
            });

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            log.warn("Authentication failed: password mismatch for userId={}", user.getId());
            throw new InvalidCredentialsException();
        }

        if (user.isBanned()) {
            log.warn("Authentication rejected: account is banned for userId={}", user.getId());
            throw new AccountBannedException();
        }

        log.debug("Credentials and account status verified for userId={} email={}", user.getId(), user.getEmail());
        return user;
    }
}
