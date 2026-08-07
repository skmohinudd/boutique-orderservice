package com.boutique.order.entity;
import jakarta.persistence.*;import java.math.BigDecimal;import java.util.UUID;
@Entity @Table(name="order_items") public class OrderItemEntity {
 @Id private UUID id; @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="order_id") private OrderEntity order; @Column(name="product_id",nullable=false) private UUID productId;
 @Column(nullable=false) private String sku; @Column(nullable=false) private String name; @Column(name="unit_price",nullable=false,precision=19,scale=2) private BigDecimal unitPrice; @Column(nullable=false) private int quantity; @Column(name="line_total",nullable=false,precision=19,scale=2) private BigDecimal lineTotal;
 protected OrderItemEntity(){} public OrderItemEntity(UUID productId,String sku,String name,BigDecimal unitPrice,int quantity,BigDecimal lineTotal){id=UUID.randomUUID();this.productId=productId;this.sku=sku;this.name=name;this.unitPrice=unitPrice;this.quantity=quantity;this.lineTotal=lineTotal;} void attach(OrderEntity o){order=o;}
 public UUID getProductId(){return productId;} public String getSku(){return sku;} public String getName(){return name;} public BigDecimal getUnitPrice(){return unitPrice;} public int getQuantity(){return quantity;} public BigDecimal getLineTotal(){return lineTotal;}
}
