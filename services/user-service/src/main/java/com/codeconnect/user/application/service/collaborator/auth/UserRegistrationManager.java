package com.codeconnect.user.application.service.collaborator.auth;

import com.codeconnect.user.application.dto.request.UserRegistrationRequest;
import com.codeconnect.user.application.service.collaborator.mentor.MentorApprovalManager;
import com.codeconnect.user.domain.enums.UserRole;
import com.codeconnect.user.domain.exception.EmailAlreadyExistsException;
import com.codeconnect.user.domain.model.User;
import com.codeconnect.user.domain.repository.UserRepository;
import com.codeconnect.user.domain.valueobject.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Collaborator responsible for orchestrating user registration,
 * email uniqueness enforcement, password encoding, and role-specific user creation.
 * Strictly adheres to Single Responsibility Principle (SRP).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserRegistrationManager {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MentorApprovalManager mentorApprovalManager;

    public User register(UserRegistrationRequest request) {
        String normalizedEmail = new Email(request.email()).value();

        if (userRepository.existsByEmail(normalizedEmail)) {
            log.warn("Registration rejected: duplicate email detected for email={}", normalizedEmail);
            throw new EmailAlreadyExistsException(normalizedEmail);
        }

        String passwordHash = passwordEncoder.encode(request.password());

        if (request.role() == UserRole.ROLE_MENTOR) {
            return registerMentor(request, normalizedEmail, passwordHash);
        }
        return registerStudent(request, normalizedEmail, passwordHash);
    }

    private User registerMentor(UserRegistrationRequest request, String email, String passwordHash) {
        User mentor = User.createMentor(email, passwordHash, request.displayName());
        User savedMentor = userRepository.save(mentor);

        mentorApprovalManager.createPendingApplication(
            savedMentor.getId(),
            savedMentor.getEmail(),
            request.linkedInUrl(),
            request.bio()
        );
        log.info("Registered pending mentor id={} email={}", savedMentor.getId(), savedMentor.getEmail());
        return savedMentor;
    }

    private User registerStudent(UserRegistrationRequest request, String email, String passwordHash) {
        User student = User.createStudent(email, passwordHash, request.displayName());
        User savedStudent = userRepository.save(student);
        log.info("Registered active student id={} email={}", savedStudent.getId(), savedStudent.getEmail());
        return savedStudent;
    }
}
