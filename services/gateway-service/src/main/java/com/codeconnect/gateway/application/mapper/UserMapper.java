package com.codeconnect.gateway.application.mapper;

import com.codeconnect.gateway.application.dto.request.SignupRequest;
import com.codeconnect.gateway.application.dto.response.UserResponse;
import com.codeconnect.gateway.domain.model.User;
import com.codeconnect.gateway.domain.enums.UserRole;
import com.codeconnect.gateway.domain.enums.UserStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Data mapper translating between presentation DTO records and User domain entities.
 * Injects PasswordEncoder directly so callers do not pass security concerns through the service layer.
 */
@Component
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public UserMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public String normalizeEmail(String email) {
        return (email == null) ? "" : email.trim().toLowerCase();
    }

    public User toEntity(SignupRequest request, String normalizedEmail) {
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

    public UserResponse toResponse(User user) {
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
