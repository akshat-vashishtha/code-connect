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
        String normalizedEmail = request.email().trim().toLowerCase();

        // Validate mentor-specific prerequisite fields
        if (request.role() == UserRole.ROLE_MENTOR) {
            if (request.linkedInUrl() == null || request.linkedInUrl().isBlank()) {
                return Mono.error(new ValidationException("LinkedIn URL is required for mentor registration"));
            }
            if (request.bio() == null || request.bio().isBlank()) {
                return Mono.error(new ValidationException("Professional bio is required for mentor registration"));
            }
        }

        return userRepository.existsByEmail(normalizedEmail)
            .flatMap(exists -> {
                if (Boolean.TRUE.equals(exists)) {
                    log.warn("Registration rejected: duplicate email detected for email={}", normalizedEmail);
                    return Mono.error(new EmailAlreadyExistsException(normalizedEmail));
                }

                UserStatus initialStatus = (request.role() == UserRole.ROLE_MENTOR)
                    ? UserStatus.PENDING_APPROVAL
                    : UserStatus.ACTIVE;

                User newUser = User.builder()
                    .email(normalizedEmail)
                    .passwordHash(passwordEncoder.encode(request.password()))
                    .displayName(request.displayName().trim())
                    .role(request.role())
                    .status(initialStatus)
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();

                return userRepository.save(newUser)
                    .flatMap(savedUser -> {
                        Mono<Void> mentorAuditMono = Mono.empty();
                        if (savedUser.getRole() == UserRole.ROLE_MENTOR) {
                            MentorApprovalRequest approvalRequest = MentorApprovalRequest.builder()
                                .userId(savedUser.getId())
                                .email(savedUser.getEmail())
                                .displayName(savedUser.getDisplayName())
                                .linkedInUrl(request.linkedInUrl() != null ? request.linkedInUrl().trim() : null)
                                .bio(request.bio() != null ? request.bio().trim() : null)
                                .status("PENDING")
                                .submittedAt(Instant.now())
                                .build();
                            mentorAuditMono = mentorApprovalRepository.save(approvalRequest).then();
                        }

                        return mentorAuditMono
                            .then(Mono.fromRunnable(() -> {
                                webSession.getAttributes().put("USER_ID", savedUser.getId());
                                webSession.getAttributes().put("USER_EMAIL", savedUser.getEmail());
                                webSession.getAttributes().put("USER_ROLE", savedUser.getRole().name());
                                webSession.getAttributes().put("USER_STATUS", savedUser.getStatus().name());
                            }))
                            .then(webSession.changeSessionId())
                            .thenReturn(new UserResponse(
                                savedUser.getId(),
                                savedUser.getEmail(),
                                savedUser.getDisplayName(),
                                savedUser.getRole(),
                                savedUser.getStatus(),
                                savedUser.getCreatedAt()
                            ));
                    });
            });
    }
}
