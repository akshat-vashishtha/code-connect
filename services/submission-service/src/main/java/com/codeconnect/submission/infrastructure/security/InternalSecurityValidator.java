package com.codeconnect.submission.infrastructure.security;

import com.codeconnect.submission.infrastructure.config.properties.SubmissionProperties;
import com.codeconnect.submission.infrastructure.security.chain.header.InternalSecurityHeaders;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.HexFormat;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class InternalSecurityValidator {

    private final SubmissionProperties properties;

    public boolean validateAndAuthenticate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String timestamp = request.getHeader(InternalSecurityHeaders.TIMESTAMP);
        String signature = request.getHeader(InternalSecurityHeaders.SIGNATURE);

        if (timestamp == null || signature == null) {
            return true;
        }

        try {
            long reqTime = Long.parseLong(timestamp);
            long now = System.currentTimeMillis();
            if (Math.abs(now - reqTime) > 300_000) { // 5-minute replay window
                log.warn("Internal security verification failed: timestamp expired (skew: {}ms)", Math.abs(now - reqTime));
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Security verification failed: Request timestamp expired");
                return false;
            }

            String userId = request.getHeader(InternalSecurityHeaders.USER_ID);
            String role = request.getHeader(InternalSecurityHeaders.USER_ROLE);
            String email = request.getHeader(InternalSecurityHeaders.USER_EMAIL);

            String safeUserId = userId != null ? userId : "";
            String safeRole = role != null ? role : "";
            String safeEmail = email != null ? email : "";

            String expectedHmac = calculateHmac(safeUserId, safeRole, safeEmail, timestamp, properties.hmacSecret());

            if (!MessageDigest.isEqual(expectedHmac.getBytes(StandardCharsets.UTF_8), signature.getBytes(StandardCharsets.UTF_8))) {
                log.warn("Internal security verification failed: HMAC mismatch");
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Security verification failed: Invalid HMAC signature");
                return false;
            }

            List<SimpleGrantedAuthority> authorities = safeRole.isBlank()
                ? Collections.emptyList()
                : List.of(new SimpleGrantedAuthority(safeRole.startsWith("ROLE_") ? safeRole : "ROLE_" + safeRole));

            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(safeUserId, safeEmail, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            return true;

        } catch (NumberFormatException e) {
            log.warn("Invalid timestamp header format");
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Security verification failed: Invalid timestamp format");
            return false;
        }
    }

    private static String calculateHmac(String userId, String role, String email, String timestamp, String secret) {
        try {
            String payload = userId + ":" + role + ":" + email + ":" + timestamp;
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(keySpec);
            byte[] rawHmac = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(rawHmac);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to calculate HMAC signature", e);
        }
    }
}
