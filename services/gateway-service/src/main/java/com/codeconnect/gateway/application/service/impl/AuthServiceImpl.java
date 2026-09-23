package com.codeconnect.gateway.application.service.impl;

import com.codeconnect.gateway.application.dto.request.SignupRequest;
import com.codeconnect.gateway.application.dto.response.UserResponse;
import com.codeconnect.gateway.application.mapper.UserMapper;
import com.codeconnect.gateway.application.service.AuthService;
import com.codeconnect.gateway.application.validator.MentorRegistrationValidator;
import com.codeconnect.gateway.domain.exception.EmailAlreadyExistsException;
import com.codeconnect.gateway.domain.model.MentorApprovalRequest;
import com.codeconnect.gateway.domain.model.User;
import com.codeconnect.gateway.domain.model.UserRole;
import com.codeconnect.gateway.domain.repository.ReactiveMentorApprovalRepository;
import com.codeconnect.gateway.domain.repository.ReactiveUserRepository;
import com.codeconnect.gateway.infrastructure.session.SessionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.time.Instant;

/**
 * High-level business facade orchestrating user onboarding workflows.
 * Adheres strictly to the Service as Facade pattern: delegates validation,
 * DTO mapping, and session state management to dedicated collaborator components.
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final ReactiveUserRepository userRepository;
    private final ReactiveMentorApprovalRepository mentorApprovalRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final MentorRegistrationValidator mentorValidator;
    private final SessionManager sessionManager;

    public AuthServiceImpl(
        ReactiveUserRepository userRepository,
        ReactiveMentorApprovalRepository mentorApprovalRepository,
        PasswordEncoder passwordEncoder,
        UserMapper userMapper,
        MentorRegistrationValidator mentorValidator,
        SessionManager sessionManager
    ) {
        this.userRepository = userRepository;
        this.mentorApprovalRepository = mentorApprovalRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.mentorValidator = mentorValidator;
        this.sessionManager = sessionManager;
    }

    @Override
    public Mono<UserResponse> signup(SignupRequest request, WebSession webSession) {
        String normalizedEmail = userMapper.normalizeEmail(request.email());

        return mentorValidator.validate(request)
            .then(ensureEmailIsAvailable(normalizedEmail))
            .then(saveNewUser(request, normalizedEmail))
            .flatMap(savedUser -> recordMentorAuditIfApplicable(request, savedUser)
                .then(sessionManager.establishSession(savedUser, webSession))
                .thenReturn(userMapper.toResponse(savedUser)));
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
        User user = userMapper.toEntity(request, normalizedEmail, passwordEncoder);
        return userRepository.save(user);
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
}
