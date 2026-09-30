package com.codeconnect.user.infrastructure.messaging;

import com.codeconnect.user.domain.event.MentorApprovedEvent;
import com.codeconnect.user.domain.event.MentorRejectedEvent;
import com.codeconnect.user.domain.event.UserRegisteredEvent;
import com.codeconnect.user.infrastructure.config.properties.KafkaTopicProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Kafka publisher that bridges in-process Spring domain events to cross-service Kafka topics.
 *
 * <p>Each listener is annotated with {@code @TransactionalEventListener(AFTER_COMMIT)} to guarantee
 * that the domain event only reaches Kafka <em>after</em> the MongoDB transaction has committed
 * successfully. This acts as a lightweight half-Outbox: if the transaction rolls back, no
 * Kafka message is ever sent — eliminating the phantom-event problem.
 *
 * <p>All listeners are fire-and-forget with non-blocking {@code whenComplete} logging.
 * The Kafka producer is configured with {@code acks=all} and {@code enable.idempotence=true}
 * to guarantee at-least-once delivery with exactly-once producer semantics.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserDomainEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopicProperties topicProperties;

    /**
     * Publishes a {@link MentorApprovedEvent} to Kafka after the approval transaction commits.
     * Partition key is {@code userId} to guarantee per-user event ordering.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMentorApproved(MentorApprovedEvent event) {
        String topic = topicProperties.topics().mentorApplicationApproved();
        String key = event.userId();

        log.info("Publishing MentorApprovedEvent to topic={} userId={} applicationId={}",
            topic, key, event.applicationId());

        kafkaTemplate.send(topic, key, event)
            .whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to publish MentorApprovedEvent for userId={}: {}", key, ex.getMessage(), ex);
                } else {
                    log.debug("Published MentorApprovedEvent userId={} topic={} partition={} offset={}",
                        key, topic, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
                }
            });
    }

    /**
     * Publishes a {@link MentorRejectedEvent} to Kafka after the rejection transaction commits.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMentorRejected(MentorRejectedEvent event) {
        String topic = topicProperties.topics().mentorApplicationRejected();
        String key = event.userId();

        log.info("Publishing MentorRejectedEvent to topic={} userId={} applicationId={}",
            topic, key, event.applicationId());

        kafkaTemplate.send(topic, key, event)
            .whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to publish MentorRejectedEvent for userId={}: {}", key, ex.getMessage(), ex);
                } else {
                    log.debug("Published MentorRejectedEvent userId={} topic={} partition={} offset={}",
                        key, topic, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
                }
            });
    }

    /**
     * Publishes a {@link UserRegisteredEvent} to Kafka after the registration transaction commits.
     * Partition key is {@code userId}.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUserRegistered(UserRegisteredEvent event) {
        String topic = topicProperties.topics().userRegistered();
        String key = event.userId();

        log.info("Publishing UserRegisteredEvent to topic={} userId={} email={} role={}",
            topic, key, event.email(), event.role());

        kafkaTemplate.send(topic, key, event)
            .whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to publish UserRegisteredEvent for userId={}: {}", key, ex.getMessage(), ex);
                } else {
                    log.debug("Published UserRegisteredEvent userId={} topic={} partition={} offset={}",
                        key, topic, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
                }
            });
    }
}
