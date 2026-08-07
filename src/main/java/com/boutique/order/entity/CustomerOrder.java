package com.boutique.order.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class CustomerOrder {
    @Id private UUID id;
    @Column(name = "user_id", nullable = false) private UUID userId;
    @Column(name = "idempotency_key", nullable = false, unique = true, length = 100) private String idempotencyKey;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30) private OrderStatus status;
    @Column(nullable = false, precision = 19, scale = 2) private BigDecimal total;
    @Column(nullable = false, length = 3) private String currency;
    @Column(name = "payment_id") private UUID paymentId;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;
    @Version @Column(nullable = false) private long version;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderItem> items = new ArrayList<>();

    protected CustomerOrder() {}

    public CustomerOrder(UUID id, UUID userId, String idempotencyKey, BigDecimal total, String currency) {
        this.id = id;
        this.userId = userId;
        this.idempotencyKey = idempotencyKey;
        this.total = total;
        this.currency = currency;
        this.status = OrderStatus.PENDING_PAYMENT;
    }

    @PrePersist void createTimestamps() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate void updateTimestamp() { updatedAt = Instant.now(); }

    public void addItem(OrderItem item) {
        item.attach(this);
        items.add(item);
    }

    public void confirmPayment(UUID paymentId) {
        this.paymentId = paymentId;
        this.status = OrderStatus.CONFIRMED;
    }

    public void failPayment() { this.status = OrderStatus.PAYMENT_FAILED; }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public OrderStatus getStatus() { return status; }
    public BigDecimal getTotal() { return total; }
    public String getCurrency() { return currency; }
    public UUID getPaymentId() { return paymentId; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public long getVersion() { return version; }
    public List<OrderItem> getItems() { return List.copyOf(items); }
}
