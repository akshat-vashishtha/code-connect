package com.codeconnect.user.application.service.collaborator.auth;

import com.codeconnect.user.application.dto.request.UserRegistrationRequest;
import com.codeconnect.user.application.service.collaborator.mentor.MentorApprovalManager;
import com.codeconnect.user.domain.enums.UserRole;
import com.codeconnect.user.domain.event.UserRegisteredEvent;
import com.codeconnect.user.domain.exception.EmailAlreadyExistsException;
import com.codeconnect.user.domain.model.UserDocument;
import com.codeconnect.user.domain.repository.UserRepository;
import com.codeconnect.user.domain.valueobject.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Collaborator responsible for orchestrating user registration,
 * email uniqueness enforcement, password encoding, and role-specific user creation.
 *
 * <p>Publishes a {@link UserRegisteredEvent} via {@link ApplicationEventPublisher} after each
 * successful registration. The event is bridged to Kafka by
 * {@link com.codeconnect.user.infrastructure.messaging.UserDomainEventPublisher}
 * via {@code @TransactionalEventListener(AFTER_COMMIT)}.
 *
 * Strictly adheres to Single Responsibility Principle (SRP).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserRegistrationManager {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MentorApprovalManager mentorApprovalManager;
    private final ApplicationEventPublisher eventPublisher;

    public UserDocument register(UserRegistrationRequest request) {
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

    private UserDocument registerMentor(UserRegistrationRequest request, String email, String passwordHash) {
        UserDocument mentor = UserDocument.createMentor(email, passwordHash, request.displayName());
        UserDocument savedMentor = userRepository.save(mentor);

        mentorApprovalManager.createPendingApplication(
            savedMentor.getId(),
            savedMentor.getEmail(),
            request.linkedInUrl(),
            request.bio()
        );
        log.info("Registered pending mentor id={} email={}", savedMentor.getId(), savedMentor.getEmail());

        publishRegistrationEvent(savedMentor);
        return savedMentor;
    }

    private UserDocument registerStudent(UserRegistrationRequest request, String email, String passwordHash) {
        UserDocument student = UserDocument.createStudent(email, passwordHash, request.displayName());
        UserDocument savedStudent = userRepository.save(student);
        log.info("Registered active student id={} email={}", savedStudent.getId(), savedStudent.getEmail());

        publishRegistrationEvent(savedStudent);
        return savedStudent;
    }

    private void publishRegistrationEvent(UserDocument savedUser) {
        eventPublisher.publishEvent(new UserRegisteredEvent(
            savedUser.getId(),
            savedUser.getEmail(),
            savedUser.getDisplayName(),
            savedUser.getRole(),
            Instant.now()
        ));
    }
}
