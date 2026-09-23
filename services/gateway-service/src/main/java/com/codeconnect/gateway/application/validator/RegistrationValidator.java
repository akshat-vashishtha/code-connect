package com.codeconnect.gateway.application.validator;

import com.codeconnect.gateway.application.dto.request.SignupRequest;
import com.codeconnect.gateway.domain.exception.EmailAlreadyExistsException;
import com.codeconnect.gateway.domain.exception.ValidationException;
import com.codeconnect.gateway.domain.enums.UserRole;
import com.codeconnect.gateway.domain.repository.ReactiveUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Domain validator ensuring pre-registration invariants:
 * mentor qualification fields and system-wide email uniqueness.
 */
@Slf4j
@Component
public class RegistrationValidator {

    private final ReactiveUserRepository userRepository;

    public RegistrationValidator(ReactiveUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<Void> validate(SignupRequest request, String normalizedEmail) {
        return validateMentorPrerequisites(request)
            .then(ensureEmailIsAvailable(normalizedEmail));
    }

    private Mono<Void> validateMentorPrerequisites(SignupRequest request) {
        if (request.role() != UserRole.ROLE_MENTOR) {
            return Mono.empty();
        }

        if (request.linkedInUrl() == null || request.linkedInUrl().isBlank()) {
            return Mono.error(new ValidationException("LinkedIn URL is required for mentor registration"));
        }

        if (request.bio() == null || request.bio().isBlank()) {
            return Mono.error(new ValidationException("Professional bio is required for mentor registration"));
        }

        return Mono.empty();
    }

    private Mono<Void> ensureEmailIsAvailable(String email) {
        return userRepository.existsByEmail(email)
            .flatMap(exists -> {
                if (Boolean.TRUE.equals(exists)) {
                    log.warn("Registration rejected: duplicate email detected for email={}", email);
                    return Mono.error(new EmailAlreadyExistsException(email));
                }
                return Mono.empty();
            });
    }
}
