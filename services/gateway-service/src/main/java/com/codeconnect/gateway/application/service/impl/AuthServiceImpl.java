package com.codeconnect.gateway.application.service.impl;

import com.codeconnect.gateway.application.dto.request.SignupRequest;
import com.codeconnect.gateway.application.dto.response.UserResponse;
import com.codeconnect.gateway.application.mapper.UserMapper;
import com.codeconnect.gateway.application.service.AuthService;
import com.codeconnect.gateway.application.service.MentorApprovalService;
import com.codeconnect.gateway.application.validator.RegistrationValidator;
import com.codeconnect.gateway.domain.repository.ReactiveUserRepository;
import com.codeconnect.gateway.infrastructure.session.SessionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

/**
 * High-level business facade orchestrating user onboarding workflows.
 * Adheres strictly to the Service as Facade pattern: delegates validation,
 * DTO mapping, mentor audit recording, and session state management to dedicated collaborator components.
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final ReactiveUserRepository userRepository;
    private final UserMapper userMapper;
    private final RegistrationValidator registrationValidator;
    private final MentorApprovalService mentorApprovalService;
    private final SessionManager sessionManager;

    public AuthServiceImpl(
        ReactiveUserRepository userRepository,
        UserMapper userMapper,
        RegistrationValidator registrationValidator,
        MentorApprovalService mentorApprovalService,
        SessionManager sessionManager
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.registrationValidator = registrationValidator;
        this.mentorApprovalService = mentorApprovalService;
        this.sessionManager = sessionManager;
    }

    @Override
    public Mono<UserResponse> signup(SignupRequest request, WebSession webSession) {
        String normalizedEmail = userMapper.normalizeEmail(request.email());

        return registrationValidator.validate(request, normalizedEmail)
            .then(userRepository.save(userMapper.toEntity(request, normalizedEmail)))
            .flatMap(savedUser -> mentorApprovalService.recordSubmission(savedUser, request)
                .then(sessionManager.establishSession(savedUser, webSession))
                .thenReturn(userMapper.toResponse(savedUser)));
    }
}
