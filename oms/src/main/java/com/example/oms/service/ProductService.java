package com.example.oms.service;

import com.example.oms.model.Product;
import java.util.UUID;

public interface ProductService {
    Product create(Product product);
    Product findById(UUID id);
    Product update(UUID id, Product product);
    void delete(UUID id);
    void adjustStock(UUID id, int delta);
}
