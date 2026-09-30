package com.codeconnect.user.application.mapper;

import com.codeconnect.user.application.dto.response.UserResponse;
import com.codeconnect.user.domain.model.UserDocument;
import org.springframework.stereotype.Component;

/**
 * Pure data mapper translating between UserDocument domain entities and response DTOs.
 * Free of security hashing concerns (satisfies Point B / SRP).
 */
@Component
public class UserMapper {

    public UserResponse toResponse(UserDocument user) {
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
