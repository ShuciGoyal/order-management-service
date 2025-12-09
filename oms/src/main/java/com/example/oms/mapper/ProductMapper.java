package com.example.oms.mapper;

import com.example.oms.dto.request.CreateProductRequest;
import com.example.oms.dto.response.ProductResponse;
import com.example.oms.model.Product;

public final class ProductMapper {
    private ProductMapper() {}

    public static Product toEntity(CreateProductRequest req) {
        if (req == null) return null;
        var p = new Product();
        p.setSku(req.sku());
        p.setName(req.name());
        p.setPriceCents(req.priceCents());
        p.setStockQuantity(req.stockQuantity());
        return p;
    }

    public static ProductResponse toResponse(Product p) {
        if (p == null) return null;
        return new ProductResponse(p.getId(), p.getSku(), p.getName(), p.getPriceCents(), p.getStockQuantity());
    }
}
