package com.example.oms.web;

import com.example.oms.dto.request.CreateProductRequest;
import com.example.oms.model.Product;
import com.example.oms.service.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

class ProductControllerTest {

    @Test
    void createProduct_unit() {
        ProductService productService = Mockito.mock(ProductService.class);
        var controller = new ProductController(productService);

        var req = new CreateProductRequest("SKU-1", "Widget", 1999L, 10);
        var saved = new Product();
        UUID id = UUID.randomUUID();
        saved.setId(id);
        saved.setSku(req.sku());
        saved.setName(req.name());

        Mockito.when(productService.create(any(Product.class))).thenReturn(saved);

        var res = controller.create(req);
    assertThat(res.getStatusCode().value()).isEqualTo(201);
        assertThat(res.getHeaders().getLocation().toString()).endsWith(id.toString());
    }

    @Test
    void getProduct_unit() {
        ProductService productService = Mockito.mock(ProductService.class);
        var controller = new ProductController(productService);

        UUID id = UUID.randomUUID();
        var p = new Product(); p.setId(id); p.setSku("SKU-1"); p.setName("W");
        Mockito.when(productService.findById(id)).thenReturn(p);

        var res = controller.get(id);
    assertThat(res.getStatusCode().value()).isEqualTo(200);
        assertThat(res.getBody()).isNotNull();
    }

    @Test
    void updateAndDelete() {
        ProductService productService = Mockito.mock(ProductService.class);
        var controller = new ProductController(productService);

        UUID id = UUID.randomUUID();
        var req = new CreateProductRequest("SKU-2", "New", 2999L, 5);
        var updated = new Product(); updated.setId(id); updated.setSku(req.sku()); updated.setName(req.name());
        Mockito.when(productService.update(Mockito.eq(id), Mockito.any(Product.class))).thenReturn(updated);

        var up = controller.update(id, req);
        assertThat(up.getStatusCode().value()).isEqualTo(200);

        Mockito.doNothing().when(productService).delete(id);
        var del = controller.delete(id);
        assertThat(del.getStatusCode().value()).isEqualTo(204);
    }
}
