package com.codeconnect.user.application.service;

import com.codeconnect.user.domain.exception.ResourceNotFoundException;
import com.codeconnect.user.domain.model.User;
import com.codeconnect.user.domain.enums.UserRole;
import com.codeconnect.user.domain.enums.UserStatus;
import com.codeconnect.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Collaborator responsible for executing user account elevation state transitions in persistence.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserAccountElevator {

    private final UserRepository userRepository;

    public User elevateToMentor(String userId, String email) {
        User user = userRepository.findById(userId)
            .or(() -> userRepository.findByEmail(email))
            .orElseThrow(() -> new ResourceNotFoundException("User associated with application not found"));

        user.elevateToMentor();

        log.debug("Elevated user id={} email={} to ROLE_MENTOR and ACTIVE", user.getId(), user.getEmail());
        return userRepository.save(user);
    }
}
