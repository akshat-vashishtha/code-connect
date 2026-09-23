package com.codeconnect.gateway.application.service;

import com.codeconnect.gateway.application.dto.request.SignupRequest;
import com.codeconnect.gateway.domain.model.User;
import reactor.core.publisher.Mono;

/**
 * Service managing mentor verification lifecycle and audit records.
 */
public interface MentorApprovalService {

    /**
     * Records a mentor approval request audit entry if the registered user is a mentor.
     *
     * @param user the registered user entity
     * @param request the signup request containing qualification details
     * @return Mono completing when recorded
     */
    Mono<Void> recordSubmission(User user, SignupRequest request);
}
