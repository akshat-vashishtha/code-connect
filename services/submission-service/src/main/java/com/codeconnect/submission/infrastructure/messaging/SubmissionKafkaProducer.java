package com.codeconnect.submission.infrastructure.messaging;

import com.codeconnect.submission.domain.event.CodeExecutionRequestedPayload;
import com.codeconnect.submission.domain.event.EventEnvelope;
import com.codeconnect.submission.infrastructure.config.properties.KafkaTopicProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Production-grade Kafka producer publishing standardized EventEnvelope records.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SubmissionKafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopicProperties topicProperties;

    public void publishSubmissionEvent(EventEnvelope<CodeExecutionRequestedPayload> envelope) {
        String topic = topicProperties.topics().submissionRequested();
        String key = envelope.payload().submissionId();

        log.info("Publishing EventEnvelope<{}> to topic={} eventId={} submissionId={} studentId={}",
            envelope.header().eventType(), topic, envelope.header().eventId(), key, envelope.payload().studentId());

        kafkaTemplate.send(topic, key, envelope)
            .whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to publish EventEnvelope for submissionId={}: {}", key, ex.getMessage(), ex);
                } else {
                    log.debug("Successfully published EventEnvelope for submissionId={} topic={} partition={} offset={}",
                        key, topic, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
                }
            });
    }
}
