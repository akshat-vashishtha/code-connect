package com.codeconnect.user.infrastructure.session;

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
public class SessionElevationManager {

    private static final String SESSIONS_PATTERN = "spring:session:sessions:*";
    private static final String ATTR_USER_ID = "sessionAttr:USER_ID";
    private static final String ATTR_USER_EMAIL = "sessionAttr:USER_EMAIL";
    private static final String ATTR_USER_ROLE = "sessionAttr:USER_ROLE";
    private static final String ATTR_USER_STATUS = "sessionAttr:USER_STATUS";

    private final StringRedisTemplate redisTemplate;

    public SessionElevationManager(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

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

            for (String key : sessionKeys) {
                // Ensure key is the session hash root, not expiration/timeline sets
                if (key.endsWith(":expires")) {
                    continue;
                }

                Object sessionUserId = redisTemplate.opsForHash().get(key, ATTR_USER_ID);
                Object sessionEmail = redisTemplate.opsForHash().get(key, ATTR_USER_EMAIL);

                boolean match = (userId != null && userId.equals(sessionUserId)) ||
                                (email != null && email.equalsIgnoreCase(String.valueOf(sessionEmail)));

                if (match) {
                    log.info("Elevating active Redis session key={} for userId={} to ROLE_MENTOR", key, userId);
                    redisTemplate.opsForHash().put(key, ATTR_USER_ROLE, "ROLE_MENTOR");
                    redisTemplate.opsForHash().put(key, ATTR_USER_STATUS, "ACTIVE");
                }
            }
        } catch (Exception ex) {
            log.warn("Failed to complete Redis session elevation scan: {}", ex.getMessage());
        }
    }
}
