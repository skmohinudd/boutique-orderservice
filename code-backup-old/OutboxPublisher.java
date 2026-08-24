package com.boutique.order.outbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Component
public class OutboxPublisher {

    private static final Logger log =
            LoggerFactory.getLogger(OutboxPublisher.class);

    private final OutboxEventRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OutboxPublisher(
            OutboxEventRepository repository,
            KafkaTemplate<String, String> kafkaTemplate
    ) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(
            fixedDelayString =
                    "${outbox.publish-delay-ms:1000}"
    )
    @Transactional
    public void publishPendingEvents() {
        for (OutboxEvent event :
                repository
                        .findTop100ByPublishedAtIsNullOrderByCreatedAtAsc()) {
            try {
                kafkaTemplate.send(
                                event.getTopic(),
                                event.getEventKey(),
                                event.getPayload()
                        )
                        .get(10, TimeUnit.SECONDS);

                event.markPublished();
            } catch (Exception exception) {
                log.error(
                        "Failed to publish outbox event {}",
                        event.getId(),
                        exception
                );

                // Leave it unpublished. A later scheduled run retries it.
                break;
            }
        }
    }
}
