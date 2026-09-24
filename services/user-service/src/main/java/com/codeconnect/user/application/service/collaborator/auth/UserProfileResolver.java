package com.codeconnect.user.application.service.collaborator.auth;

import com.codeconnect.user.domain.exception.ResourceNotFoundException;
import com.codeconnect.user.domain.model.User;
import com.codeconnect.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Collaborator responsible for querying and resolving user profiles from persistence.
 * Strictly adheres to Single Responsibility Principle (SRP).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserProfileResolver {

    private final UserRepository userRepository;

    public User resolveById(String userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> {
                log.warn("User profile resolution failed: user not found for userId={}", userId);
                return new ResourceNotFoundException("User not found: " + userId);
            });
    }
}
