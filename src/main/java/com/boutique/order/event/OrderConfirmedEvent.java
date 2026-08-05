package com.boutique.order.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderConfirmedEvent(
        UUID eventId,
        String eventType,
        int eventVersion,
        UUID aggregateId,
        Instant occurredAt,
        Data data
) {
    public record Data(
            UUID orderId,
            UUID userId,
            UUID paymentId,
            BigDecimal total,
            String currency
    ) {
    }

    public static OrderConfirmedEvent create(
            UUID orderId,
            UUID userId,
            UUID paymentId,
            BigDecimal total,
            String currency
    ) {
        return new OrderConfirmedEvent(
                UUID.randomUUID(),
                "OrderConfirmed",
                1,
                orderId,
                Instant.now(),
                new Data(
                        orderId,
                        userId,
                        paymentId,
                        total,
                        currency
                )
        );
    }
}
