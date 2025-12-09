package com.example.oms.mapper;

import com.example.oms.dto.request.CreateProductRequest;
 
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductMapperTest {

    @Test
    void toEntity_and_toResponse() {
        var req = new CreateProductRequest("SKU-1", "Widget", 1999L, 10);
        var p = ProductMapper.toEntity(req);
        assertThat(p.getSku()).isEqualTo("SKU-1");
        assertThat(p.getName()).isEqualTo("Widget");
        assertThat(p.getPriceCents()).isEqualTo(1999L);
        assertThat(p.getStockQuantity()).isEqualTo(10);
    }
}
