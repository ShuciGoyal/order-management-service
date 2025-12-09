package com.example.oms.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @Column(length = 36)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "unit_price_cents", nullable = false)
    private long unitPriceCents;

    @Column(nullable = false)
    private int quantity;

    public OrderItem() {}

    @PrePersist
    public void prePersist() { if (id == null) id = UUID.randomUUID(); }

    // Getters / setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public long getUnitPriceCents() { return unitPriceCents; }
    public void setUnitPriceCents(long unitPriceCents) { this.unitPriceCents = unitPriceCents; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
