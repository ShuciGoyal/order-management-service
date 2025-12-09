package com.example.oms.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @Column(length = 36)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String sku;

    @Column(nullable = false)
    private String name;

    @Column(name = "price_cents", nullable = false)
    private long priceCents;

    @Column(name = "stock_quantity", nullable = false)
    private int stockQuantity;

    @Version
    private Long version;

    public Product() {}

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
    }

    // Getters / setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public long getPriceCents() { return priceCents; }
    public void setPriceCents(long priceCents) { this.priceCents = priceCents; }
    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
