package com.boutique.order.repository;

import com.boutique.order.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<OutboxEvent,UUID> {
    @Query(value="""
        select * from outbox_events
        where kafka_published_at is null or rabbit_published_at is null
        order by created_at
        limit :limit
        for update skip locked
        """, nativeQuery=true)
    List<OutboxEvent> lockBatch(@Param("limit") int limit);
}
