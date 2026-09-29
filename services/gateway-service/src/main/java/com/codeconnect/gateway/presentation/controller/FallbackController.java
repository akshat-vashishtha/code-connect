package com.codeconnect.gateway.presentation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Instant;

/**
 * Reactive fallback boundary for CircuitBreaker tripped states and timeouts.
 * Produces RFC 7807 ProblemDetail envelopes with HTTP 503 SERVICE_UNAVAILABLE.
 */
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @RequestMapping("/user-service")
    public Mono<ResponseEntity<ProblemDetail>> userServiceFallback() {
        return Mono.just(buildDegradedResponse("User Service is temporarily unavailable or experiencing high load. Please try again shortly."));
    }

    @RequestMapping("/curriculum-service")
    public Mono<ResponseEntity<ProblemDetail>> curriculumServiceFallback() {
        return Mono.just(buildDegradedResponse("Curriculum Service is temporarily unavailable or experiencing high load. Please try again shortly."));
    }

    @RequestMapping("/submission-service")
    public Mono<ResponseEntity<ProblemDetail>> submissionServiceFallback() {
        return Mono.just(buildDegradedResponse("Code Submission Service is temporarily unavailable or experiencing high load. Please try again shortly."));
    }

    @RequestMapping("/collab-service")
    public Mono<ResponseEntity<ProblemDetail>> collabServiceFallback() {
        return Mono.just(buildDegradedResponse("Collaboration & AI Service is temporarily unavailable or experiencing high load. Please try again shortly."));
    }

    private ResponseEntity<ProblemDetail> buildDegradedResponse(String detailMessage) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.SERVICE_UNAVAILABLE,
            detailMessage
        );
        problem.setTitle("Service Degraded");
        problem.setProperty("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(problem);
    }
}
