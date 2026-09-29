package com.codeconnect.gateway.presentation.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

class FallbackControllerTest {

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToController(new FallbackController()).build();
    }

    @Test
    void userServiceFallback_returns503ServiceUnavailable() {
        webTestClient.get()
            .uri("/fallback/user-service")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("$.status").isEqualTo(503)
            .jsonPath("$.title").isEqualTo("Service Degraded")
            .jsonPath("$.detail").value(detail -> ((String) detail).contains("User Service is temporarily unavailable"));
    }

    @Test
    void curriculumServiceFallback_returns503ServiceUnavailable() {
        webTestClient.get()
            .uri("/fallback/curriculum-service")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("$.status").isEqualTo(503)
            .jsonPath("$.title").isEqualTo("Service Degraded")
            .jsonPath("$.detail").value(detail -> ((String) detail).contains("Curriculum Service is temporarily unavailable"));
    }

    @Test
    void submissionServiceFallback_returns503ServiceUnavailable() {
        webTestClient.get()
            .uri("/fallback/submission-service")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("$.status").isEqualTo(503)
            .jsonPath("$.title").isEqualTo("Service Degraded");
    }

    @Test
    void collabServiceFallback_returns503ServiceUnavailable() {
        webTestClient.get()
            .uri("/fallback/collab-service")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("$.status").isEqualTo(503)
            .jsonPath("$.title").isEqualTo("Service Degraded");
    }
}
