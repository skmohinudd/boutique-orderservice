package com.boutique.order.entity;
import jakarta.persistence.*;import java.math.BigDecimal;import java.time.Instant;import java.util.*;
@Entity @Table(name="orders",uniqueConstraints=@UniqueConstraint(name="uk_orders_idempotency",columnNames="idempotency_key"))
public class OrderEntity {
 @Id private UUID id; @Column(name="user_id",nullable=false) private UUID userId; @Column(name="idempotency_key",nullable=false,length=160) private String idempotencyKey;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=32) private OrderStatus status; @Column(nullable=false,precision=19,scale=2) private BigDecimal total; @Column(nullable=false,length=3) private String currency;
 @Column(name="payment_id") private UUID paymentId; @Column(name="created_at",nullable=false) private Instant createdAt; @Column(name="updated_at",nullable=false) private Instant updatedAt;
 @OneToMany(mappedBy="order",cascade=CascadeType.ALL,orphanRemoval=true,fetch=FetchType.EAGER) private List<OrderItemEntity> items=new ArrayList<>();
 protected OrderEntity(){} public OrderEntity(UUID userId,String key,BigDecimal total,String currency){id=UUID.randomUUID();this.userId=userId;idempotencyKey=key;this.total=total;this.currency=currency;status=OrderStatus.PENDING;createdAt=updatedAt=Instant.now();}
 public void addItem(OrderItemEntity i){items.add(i);i.attach(this);} public void confirm(UUID p){status=OrderStatus.CONFIRMED;paymentId=p;updatedAt=Instant.now();} public void paymentFailed(){status=OrderStatus.PAYMENT_FAILED;updatedAt=Instant.now();}
 public UUID getId(){return id;} public UUID getUserId(){return userId;} public String getIdempotencyKey(){return idempotencyKey;} public OrderStatus getStatus(){return status;} public BigDecimal getTotal(){return total;} public String getCurrency(){return currency;} public UUID getPaymentId(){return paymentId;} public Instant getCreatedAt(){return createdAt;} public List<OrderItemEntity> getItems(){return items;}
}
