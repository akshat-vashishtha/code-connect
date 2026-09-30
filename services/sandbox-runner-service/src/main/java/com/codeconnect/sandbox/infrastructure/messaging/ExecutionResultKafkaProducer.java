package com.codeconnect.sandbox.infrastructure.messaging;

import com.codeconnect.sandbox.domain.event.CodeExecutionCompletedPayload;
import com.codeconnect.sandbox.domain.event.EventEnvelope;
import com.codeconnect.sandbox.infrastructure.config.properties.KafkaTopicProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Production-grade Kafka producer publishing standardized EventEnvelope records for execution completion.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExecutionResultKafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopicProperties topicProperties;

    public void publishExecutionResult(EventEnvelope<CodeExecutionCompletedPayload> envelope) {
        String topic = topicProperties.topics().executionCompleted();
        String key = envelope.payload().submissionId();

        log.info("Publishing EventEnvelope<{}> to topic={} eventId={} submissionId={} status={}",
            envelope.header().eventType(), topic, envelope.header().eventId(), key, envelope.payload().status());

        kafkaTemplate.send(topic, key, envelope)
            .whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to publish ExecutionResult EventEnvelope for submissionId={}: {}", key, ex.getMessage(), ex);
                } else {
                    log.debug("Successfully published ExecutionResult EventEnvelope for submissionId={} topic={} partition={} offset={}",
                        key, topic, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
                }
            });
    }
}
