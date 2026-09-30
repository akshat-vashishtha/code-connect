package com.codeconnect.curriculum.infrastructure.messaging;

import com.codeconnect.curriculum.domain.event.LessonCreatedEvent;
import com.codeconnect.curriculum.domain.event.TrackCreatedEvent;
import com.codeconnect.curriculum.infrastructure.config.properties.KafkaTopicProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Kafka publisher that bridges in-process Spring domain events in curriculum-service
 * to cross-service Kafka topics.
 *
 * <p>Annotated with {@code @TransactionalEventListener(phase = AFTER_COMMIT, fallbackExecution = true)}
 * to guarantee that events only reach Kafka after persistence transactions succeed.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CurriculumDomainEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopicProperties topicProperties;

    /**
     * Publishes a {@link TrackCreatedEvent} to Kafka after the track creation transaction commits.
     * Partition key is {@code trackId}.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onTrackCreated(TrackCreatedEvent event) {
        String topic = topicProperties.topics().trackCreated();
        String key = event.trackId();

        log.info("Publishing TrackCreatedEvent to topic={} trackId={} slug={}", topic, key, event.slug());

        kafkaTemplate.send(topic, key, event)
            .whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to publish TrackCreatedEvent for trackId={}: {}", key, ex.getMessage(), ex);
                } else {
                    log.debug("Published TrackCreatedEvent trackId={} topic={} partition={} offset={}",
                        key, topic, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
                }
            });
    }

    /**
     * Publishes a {@link LessonCreatedEvent} to Kafka after the lesson creation transaction commits.
     * Partition key is {@code lessonId}.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onLessonCreated(LessonCreatedEvent event) {
        String topic = topicProperties.topics().lessonCreated();
        String key = event.lessonId();

        log.info("Publishing LessonCreatedEvent to topic={} lessonId={} moduleId={}", topic, key, event.moduleId());

        kafkaTemplate.send(topic, key, event)
            .whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to publish LessonCreatedEvent for lessonId={}: {}", key, ex.getMessage(), ex);
                } else {
                    log.debug("Published LessonCreatedEvent lessonId={} topic={} partition={} offset={}",
                        key, topic, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
                }
            });
    }
}
