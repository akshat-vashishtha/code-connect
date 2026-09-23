package com.codeconnect.user.application.service.impl;

import com.codeconnect.user.application.dto.request.UserAuthenticationRequest;
import com.codeconnect.user.application.dto.request.UserRegistrationRequest;
import com.codeconnect.user.application.dto.response.UserResponse;
import com.codeconnect.user.application.mapper.UserMapper;
import com.codeconnect.user.application.service.UserAuthenticationService;
import com.codeconnect.user.domain.enums.UserRole;
import com.codeconnect.user.domain.exception.AccountBannedException;
import com.codeconnect.user.domain.exception.EmailAlreadyExistsException;
import com.codeconnect.user.domain.exception.InvalidCredentialsException;
import com.codeconnect.user.domain.exception.ResourceNotFoundException;
import com.codeconnect.user.domain.model.MentorApprovalRequest;
import com.codeconnect.user.domain.model.User;
import com.codeconnect.user.domain.repository.MentorApprovalRepository;
import com.codeconnect.user.domain.repository.UserRepository;
import com.codeconnect.user.domain.valueobject.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service implementation managing user registration, credential authentication,
 * and user profile queries. Acts as authoritative boundary for user persistence.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserAuthenticationServiceImpl implements UserAuthenticationService {

    private final UserRepository userRepository;
    private final MentorApprovalRepository mentorApprovalRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserResponse register(UserRegistrationRequest request) {
        String normalizedEmail = new Email(request.email()).value();

        if (userRepository.existsByEmail(normalizedEmail)) {
            log.warn("Registration rejected: duplicate email detected for email={}", normalizedEmail);
            throw new EmailAlreadyExistsException(normalizedEmail);
        }

        String passwordHash = passwordEncoder.encode(request.password());
        User user = (request.role() == UserRole.ROLE_MENTOR)
            ? createAndRecordMentor(request, normalizedEmail, passwordHash)
            : createStudent(request, normalizedEmail, passwordHash);

        log.info("Registered user id={} email={} role={}", user.getId(), user.getEmail(), user.getRole());
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse authenticate(UserAuthenticationRequest request) {
        String normalizedEmail = new Email(request.email()).value();

        User user = userRepository.findByEmail(normalizedEmail)
            .orElseThrow(() -> {
                log.warn("Authentication failed: email not found for email={}", normalizedEmail);
                return new InvalidCredentialsException();
            });

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            log.warn("Authentication failed: password mismatch for userId={}", user.getId());
            throw new InvalidCredentialsException();
        }

        if (user.isBanned()) {
            log.warn("Authentication rejected: account is banned for userId={}", user.getId());
            throw new AccountBannedException();
        }

        log.info("Successfully authenticated userId={} email={}", user.getId(), user.getEmail());
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse getUserById(String userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        return userMapper.toResponse(user);
    }

    private User createAndRecordMentor(UserRegistrationRequest request, String email, String passwordHash) {
        if (request.linkedInUrl() == null || request.linkedInUrl().isBlank()) {
            throw new IllegalArgumentException("LinkedIn URL is required for mentor registration");
        }
        if (request.bio() == null || request.bio().isBlank()) {
            throw new IllegalArgumentException("Professional bio is required for mentor registration");
        }

        User user = User.createMentor(email, passwordHash, request.displayName());
        User savedUser = userRepository.save(user);

        MentorApprovalRequest approvalReq = MentorApprovalRequest.createPending(
            savedUser.getId(),
            savedUser.getEmail(),
            request.linkedInUrl(),
            request.bio()
        );
        mentorApprovalRepository.save(approvalReq);
        log.info("Recorded mentor approval request for userId={}", savedUser.getId());

        return savedUser;
    }

    private User createStudent(UserRegistrationRequest request, String email, String passwordHash) {
        User user = User.createStudent(email, passwordHash, request.displayName());
        return userRepository.save(user);
    }
}
