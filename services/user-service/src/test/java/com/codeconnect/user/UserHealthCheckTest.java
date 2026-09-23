package com.codeconnect.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.data.mongodb.uri=mongodb://localhost:27017/codeconnect_test_db",
    "spring.data.redis.host=localhost",
    "spring.data.redis.port=6379",
    "spring.session.store-type=none",
    "spring.kafka.bootstrap-servers=localhost:9092",
    "management.endpoint.health.show-details=always"
})
class UserHealthCheckTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("User Service /actuator/health should return UP status")
    void healthCheckShouldReturnUp() throws Exception {
        mockMvc.perform(get("/actuator/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UP"));
    }
}
