package com.codeconnect.gateway.application.service.impl;

import com.codeconnect.gateway.application.dto.request.SignupRequest;
import com.codeconnect.gateway.application.dto.response.UserResponse;
import com.codeconnect.gateway.application.service.AuthService;
import com.codeconnect.gateway.domain.exception.EmailAlreadyExistsException;
import com.codeconnect.gateway.domain.exception.ValidationException;
import com.codeconnect.gateway.domain.model.MentorApprovalRequest;
import com.codeconnect.gateway.domain.model.User;
import com.codeconnect.gateway.domain.model.UserRole;
import com.codeconnect.gateway.domain.model.UserStatus;
import com.codeconnect.gateway.domain.repository.ReactiveMentorApprovalRepository;
import com.codeconnect.gateway.domain.repository.ReactiveUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.time.Instant;

/**
 * Implementation of AuthService enforcing registration invariants,
 * BCrypt credential hashing, mentor approval capture, and session initialization.
 * Adheres strictly to Clean Code SLAP (Single Level of Abstraction Principle)
 * with small, focused methods under 20-30 lines.
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final ReactiveUserRepository userRepository;
    private final ReactiveMentorApprovalRepository mentorApprovalRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(
        ReactiveUserRepository userRepository,
        ReactiveMentorApprovalRepository mentorApprovalRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.mentorApprovalRepository = mentorApprovalRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Mono<UserResponse> signup(SignupRequest request, WebSession webSession) {
        String normalizedEmail = normalizeEmail(request.email());

        return validateMentorPrerequisites(request)
            .then(ensureEmailIsAvailable(normalizedEmail))
            .then(saveNewUser(request, normalizedEmail))
            .flatMap(savedUser -> recordMentorAuditIfApplicable(request, savedUser)
                .then(establishUserSession(savedUser, webSession))
                .thenReturn(toUserResponse(savedUser)));
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
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

    private Mono<User> saveNewUser(SignupRequest request, String normalizedEmail) {
        User user = buildUserEntity(request, normalizedEmail);
        return userRepository.save(user);
    }

    private User buildUserEntity(SignupRequest request, String normalizedEmail) {
        UserStatus initialStatus = (request.role() == UserRole.ROLE_MENTOR)
            ? UserStatus.PENDING_APPROVAL
            : UserStatus.ACTIVE;

        return User.builder()
            .email(normalizedEmail)
            .passwordHash(passwordEncoder.encode(request.password()))
            .displayName(request.displayName().trim())
            .role(request.role())
            .status(initialStatus)
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();
    }

    private Mono<Void> recordMentorAuditIfApplicable(SignupRequest request, User user) {
        if (user.getRole() != UserRole.ROLE_MENTOR) {
            return Mono.empty();
        }

        MentorApprovalRequest approvalRequest = MentorApprovalRequest.builder()
            .userId(user.getId())
            .email(user.getEmail())
            .displayName(user.getDisplayName())
            .linkedInUrl(request.linkedInUrl() != null ? request.linkedInUrl().trim() : null)
            .bio(request.bio() != null ? request.bio().trim() : null)
            .status("PENDING")
            .submittedAt(Instant.now())
            .build();

        return mentorApprovalRepository.save(approvalRequest).then();
    }

    private Mono<Void> establishUserSession(User user, WebSession webSession) {
        return Mono.fromRunnable(() -> {
            webSession.getAttributes().put("USER_ID", user.getId());
            webSession.getAttributes().put("USER_EMAIL", user.getEmail());
            webSession.getAttributes().put("USER_ROLE", user.getRole().name());
            webSession.getAttributes().put("USER_STATUS", user.getStatus().name());
        }).then(webSession.changeSessionId());
    }

    private UserResponse toUserResponse(User user) {
        return new UserResponse(
            user.getId(),
            user.getEmail(),
            user.getDisplayName(),
            user.getRole(),
            user.getStatus(),
            user.getCreatedAt()
        );
    }
}
