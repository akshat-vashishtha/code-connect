package com.codeconnect.curriculum.infrastructure.messaging;

import com.codeconnect.curriculum.domain.enums.TrackStatus;
import com.codeconnect.curriculum.domain.event.LessonCreatedEvent;
import com.codeconnect.curriculum.domain.event.TrackCreatedEvent;
import com.codeconnect.curriculum.infrastructure.config.properties.KafkaTopicProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurriculumDomainEventPublisherTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private KafkaTopicProperties topicProperties;
    private CurriculumDomainEventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        topicProperties = new KafkaTopicProperties(
            "test",
            new KafkaTopicProperties.Topics(
                "test.codeconnect.curriculum.track.created.v1",
                "test.codeconnect.curriculum.lesson.created.v1"
            )
        );
        eventPublisher = new CurriculumDomainEventPublisher(kafkaTemplate, topicProperties);
    }

    @Test
    @DisplayName("Should publish TrackCreatedEvent to Kafka topic with trackId as partition key")
    void shouldPublishTrackCreatedEvent() {
        TrackCreatedEvent event = new TrackCreatedEvent(
            "track-101",
            "System Design",
            "system-design",
            "Distributed systems",
            40,
            TrackStatus.PUBLISHED,
            Instant.now()
        );

        when(kafkaTemplate.send(eq("test.codeconnect.curriculum.track.created.v1"), eq("track-101"), any()))
            .thenReturn(CompletableFuture.completedFuture(null));

        eventPublisher.onTrackCreated(event);

        verify(kafkaTemplate).send(eq("test.codeconnect.curriculum.track.created.v1"), eq("track-101"), eq(event));
    }

    @Test
    @DisplayName("Should publish LessonCreatedEvent to Kafka topic with lessonId as partition key")
    void shouldPublishLessonCreatedEvent() {
        LessonCreatedEvent event = new LessonCreatedEvent(
            "lesson-202",
            "module-10",
            "Consistent Hashing",
            "consistent-hashing",
            1,
            Instant.now()
        );

        when(kafkaTemplate.send(eq("test.codeconnect.curriculum.lesson.created.v1"), eq("lesson-202"), any()))
            .thenReturn(CompletableFuture.completedFuture(null));

        eventPublisher.onLessonCreated(event);

        verify(kafkaTemplate).send(eq("test.codeconnect.curriculum.lesson.created.v1"), eq("lesson-202"), eq(event));
    }
}
