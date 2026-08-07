package com.boutique.order.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "order_id", nullable = false) private CustomerOrder order;
    @Column(name = "product_id", nullable = false) private UUID productId;
    @Column(nullable = false, length = 80) private String sku;
    @Column(nullable = false, length = 200) private String name;
    @Column(name = "unit_price", nullable = false, precision = 19, scale = 2) private BigDecimal unitPrice;
    @Column(nullable = false) private int quantity;
    @Column(name = "line_total", nullable = false, precision = 19, scale = 2) private BigDecimal lineTotal;

    protected OrderItem() {}

    public OrderItem(UUID productId, String sku, String name, BigDecimal unitPrice, int quantity, BigDecimal lineTotal) {
        this.id = UUID.randomUUID();
        this.productId = productId;
        this.sku = sku;
        this.name = name;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.lineTotal = lineTotal;
    }

    void attach(CustomerOrder order) { this.order = order; }
    public UUID getProductId() { return productId; }
    public String getSku() { return sku; }
    public String getName() { return name; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public int getQuantity() { return quantity; }
    public BigDecimal getLineTotal() { return lineTotal; }
}
