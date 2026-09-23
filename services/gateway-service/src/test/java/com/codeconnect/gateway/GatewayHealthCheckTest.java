package com.codeconnect.gateway;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
@AutoConfigureWebTestClient
@TestPropertySource(properties = {
    "spring.data.redis.host=localhost",
    "spring.data.redis.port=6379",
    "spring.session.store-type=none",
    "management.endpoint.health.show-details=always"
})
class GatewayHealthCheckTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("Gateway /actuator/health should return UP status")
    void healthCheckShouldReturnUp() {
        webTestClient.get()
            .uri("/actuator/health")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.status").isEqualTo("UP");
    }
}
