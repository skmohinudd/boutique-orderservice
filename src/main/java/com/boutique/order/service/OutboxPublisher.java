package com.boutique.order.service;

import com.boutique.order.messaging.RabbitTopologyConfig;
import com.boutique.order.repository.OutboxRepository;
import tools.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OutboxPublisher {
    private static final Logger log=LoggerFactory.getLogger(OutboxPublisher.class);
    private final OutboxRepository repo;
    private final KafkaTemplate<String,String> kafka;
    private final RabbitTemplate rabbit;
    private final ObjectMapper json;
    private final String topic;

    public OutboxPublisher(OutboxRepository repo,KafkaTemplate<String,String> kafka,RabbitTemplate rabbit,
                           ObjectMapper json,@Value("${app.kafka.order-events-topic}")String topic){
        this.repo=repo;this.kafka=kafka;this.rabbit=rabbit;this.json=json;this.topic=topic;
    }

    @Scheduled(fixedDelayString="${app.outbox.publish-delay-ms:1000}")
    @Transactional
    public void publish(){
        for(var e:repo.lockBatch(50)){
            String payload=e.getPayload();
            if(e.getKafkaPublishedAt()==null){
                try{ kafka.send(topic,e.getAggregateId().toString(),payload).join(); e.kafkaPublished(); }
                catch(Exception ex){ log.error("ORDER_OUTBOX_KAFKA_FAILED eventId={}",e.getId(),ex); }
            }
            if(e.getRabbitPublishedAt()==null){
                try{
                    String type=json.readTree(payload).path("eventType").asText("UNKNOWN");
                    rabbit.convertAndSend(RabbitTopologyConfig.EXCHANGE,"order."+type.toLowerCase(),payload);
                    e.rabbitPublished();
                }catch(Exception ex){ log.error("ORDER_OUTBOX_RABBIT_FAILED eventId={}",e.getId(),ex); }
            }
        }
    }
}
