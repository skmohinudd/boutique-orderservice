package com.boutique.order.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="outbox_events")
public class OutboxEvent {
    @Id private UUID id;
    @Column(name="aggregate_id",nullable=false) private UUID aggregateId;
    @Column(name="event_type",nullable=false) private String eventType;
    @Column(nullable=false,columnDefinition="text") private String payload;
    @Column(name="created_at",nullable=false) private Instant createdAt;
    @Column(name="published_at") private Instant publishedAt;
    @Column(name="kafka_published_at") private Instant kafkaPublishedAt;
    @Column(name="rabbit_published_at") private Instant rabbitPublishedAt;

    protected OutboxEvent() {}
    public OutboxEvent(UUID aggregateId,String eventType,String payload){
        this.id=UUID.randomUUID();this.aggregateId=aggregateId;this.eventType=eventType;
        this.payload=payload;this.createdAt=Instant.now();
    }
    public UUID getId(){return id;}
    public UUID getAggregateId(){return aggregateId;}
    public String getEventType(){return eventType;}
    public String getPayload(){return payload;}
    public Instant getKafkaPublishedAt(){return kafkaPublishedAt;}
    public Instant getRabbitPublishedAt(){return rabbitPublishedAt;}
    public void kafkaPublished(){this.kafkaPublishedAt=Instant.now();updateLegacyPublished();}
    public void rabbitPublished(){this.rabbitPublishedAt=Instant.now();updateLegacyPublished();}
    private void updateLegacyPublished(){if(kafkaPublishedAt!=null&&rabbitPublishedAt!=null&&publishedAt==null)publishedAt=Instant.now();}
}
