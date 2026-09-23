package com.codeconnect.user;

import org.bson.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.TestPropertySource;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.data.mongodb.uri=mongodb://localhost:27017/codeconnect_db",
    "spring.data.redis.host=localhost",
    "spring.data.redis.port=6379",
    "spring.session.store-type=none",
    "spring.kafka.bootstrap-servers=localhost:9092"
})
class BootstrapDatabaseTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    @DisplayName("Persist bootstrap document to materialize codeconnect_db on disk")
    void initializeCodeconnectDb() {
        Document bootstrapDoc = new Document()
            .append("platform", "CodeConnect")
            .append("version", "0.0.1-SNAPSHOT")
            .append("status", "BOOTSTRAPPED")
            .append("createdAt", new Date());

        mongoTemplate.save(bootstrapDoc, "system_init");

        assertThat(mongoTemplate.collectionExists("system_init")).isTrue();
    }
}
