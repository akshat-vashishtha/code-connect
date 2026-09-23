package com.codeconnect.user;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.data.mongodb.uri=mongodb://localhost:27017/codeconnect_test_db",
    "spring.data.redis.host=localhost",
    "spring.data.redis.port=6379",
    "spring.session.store-type=none",
    "spring.kafka.bootstrap-servers=localhost:9092"
})
class UserApplicationTests {

    @Test
    void contextLoads() {
    }
}
