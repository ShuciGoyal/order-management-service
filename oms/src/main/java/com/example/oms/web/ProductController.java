package com.example.oms.web;

import com.example.oms.dto.request.CreateProductRequest;
import com.example.oms.dto.response.ProductResponse;
import com.example.oms.mapper.ProductMapper;
import com.example.oms.model.Product;
import com.example.oms.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> create(@RequestBody @Valid CreateProductRequest req) {
        Product p = ProductMapper.toEntity(req);
        var saved = productService.create(p);
        var resp = ProductMapper.toResponse(saved);
        return ResponseEntity.created(URI.create("/api/v1/products/" + saved.getId())).body(resp);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> get(@PathVariable UUID id) {
        var p = productService.findById(id);
        return ResponseEntity.ok(ProductMapper.toResponse(p));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponse> update(@PathVariable UUID id, @RequestBody CreateProductRequest req) {
        Product p = ProductMapper.toEntity(req);
        var updated = productService.update(id, p);
        return ResponseEntity.ok(ProductMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
