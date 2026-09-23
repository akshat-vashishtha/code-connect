package com.codeconnect.user.infrastructure.session;

import com.codeconnect.user.domain.model.UserRole;
import com.codeconnect.user.domain.model.UserStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Infrastructure collaborator responsible for mutating distributed Redis session attributes.
 * Updates role and status attributes in O(1) time without requiring user re-authentication.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SessionElevationManager {

    private static final String SESSIONS_PATTERN = "spring:session:sessions:*";
    private static final String EXPIRES_SUFFIX = ":expires";
    private static final String ATTR_USER_ID = "sessionAttr:USER_ID";
    private static final String ATTR_USER_EMAIL = "sessionAttr:USER_EMAIL";
    private static final String ATTR_USER_ROLE = "sessionAttr:USER_ROLE";
    private static final String ATTR_USER_STATUS = "sessionAttr:USER_STATUS";

    private final StringRedisTemplate redisTemplate;

    /**
     * Elevates active sessions matching userId or email to ROLE_MENTOR and ACTIVE status.
     *
     * @param userId target user unique identifier
     * @param email  target user email
     */
    public void elevateUserSessions(String userId, String email) {
        try {
            Set<String> sessionKeys = redisTemplate.keys(SESSIONS_PATTERN);
            if (sessionKeys == null || sessionKeys.isEmpty()) {
                log.info("No active Redis sessions found for elevation scan");
                return;
            }

            sessionKeys.stream()
                .filter(this::isRootSessionKey)
                .forEach(key -> elevateSessionIfMatching(key, userId, email));
        } catch (Exception ex) {
            log.warn("Failed to complete Redis session elevation scan: {}", ex.getMessage());
        }
    }

    private boolean isRootSessionKey(String key) {
        return !key.endsWith(EXPIRES_SUFFIX);
    }

    private void elevateSessionIfMatching(String key, String userId, String email) {
        if (isUserSessionMatch(key, userId, email)) {
            applyMentorElevation(key, userId);
        }
    }

    private boolean isUserSessionMatch(String key, String userId, String email) {
        Object sessionUserId = redisTemplate.opsForHash().get(key, ATTR_USER_ID);
        Object sessionEmail = redisTemplate.opsForHash().get(key, ATTR_USER_EMAIL);

        return (userId != null && userId.equals(sessionUserId)) ||
               (email != null && email.equalsIgnoreCase(String.valueOf(sessionEmail)));
    }

    private void applyMentorElevation(String key, String userId) {
        log.info("Elevating active Redis session key={} for userId={} to ROLE_MENTOR and ACTIVE", key, userId);
        redisTemplate.opsForHash().put(key, ATTR_USER_ROLE, UserRole.ROLE_MENTOR.name());
        redisTemplate.opsForHash().put(key, ATTR_USER_STATUS, UserStatus.ACTIVE.name());
    }
}
