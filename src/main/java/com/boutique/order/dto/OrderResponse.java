package com.boutique.order.dto;

import com.boutique.order.entity.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        UUID userId,
        String idempotencyKey,
        OrderStatus status,
        List<Item> items,
        BigDecimal total,
        String currency,
        UUID paymentId,
        Instant createdAt,
        Instant updatedAt,
        long version
) {
    public record Item(UUID productId, String sku, String name, BigDecimal unitPrice, int quantity, BigDecimal lineTotal) {}
}
