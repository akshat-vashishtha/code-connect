package com.codeconnect.gateway.application.validator;

import com.codeconnect.gateway.application.dto.request.SignupRequest;
import com.codeconnect.gateway.domain.exception.ValidationException;
import com.codeconnect.gateway.domain.model.UserRole;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Domain validator ensuring mentor qualification requirements are satisfied.
 */
@Component
public class MentorRegistrationValidator {

    public Mono<Void> validate(SignupRequest request) {
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
}
