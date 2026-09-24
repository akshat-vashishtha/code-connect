package com.codeconnect.user.domain.model;

import com.codeconnect.user.domain.enums.UserRole;
import com.codeconnect.user.domain.enums.UserStatus;
import com.codeconnect.user.domain.valueobject.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Rich domain entity representing user accounts in the 'users' MongoDB collection.
 * Enforces domain invariants and encapsulates lifecycle state transitions.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    private String email;

    private String passwordHash;

    private String displayName;

    private UserRole role;

    private UserStatus status;

    private Instant createdAt;

    private Instant updatedAt;

    public static User createStudent(String email, String passwordHash, String displayName) {
        String validatedEmail = new Email(email).value();
        Instant now = Instant.now();
        return User.builder()
            .email(validatedEmail)
            .passwordHash(passwordHash)
            .displayName(displayName != null ? displayName.trim() : "")
            .role(UserRole.ROLE_STUDENT)
            .status(UserStatus.ACTIVE)
            .createdAt(now)
            .updatedAt(now)
            .build();
    }

    public static User createMentor(String email, String passwordHash, String displayName) {
        String validatedEmail = new Email(email).value();
        Instant now = Instant.now();
        return User.builder()
            .email(validatedEmail)
            .passwordHash(passwordHash)
            .displayName(displayName != null ? displayName.trim() : "")
            .role(UserRole.ROLE_MENTOR)
            .status(UserStatus.PENDING_APPROVAL)
            .createdAt(now)
            .updatedAt(now)
            .build();
    }

    /**
     * Elevates a pending mentor's role to ROLE_MENTOR and activates account status.
     * Encapsulates state mutation and timestamp updating.
     */
    public void elevateToMentor() {
        this.role = UserRole.ROLE_MENTOR;
        this.status = UserStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }
}
