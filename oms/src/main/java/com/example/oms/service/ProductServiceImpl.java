package com.example.oms.service;

import com.example.oms.model.Product;
import com.example.oms.repository.ProductRepository;
import com.example.oms.service.exception.InsufficientStockException;
import com.example.oms.service.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.OptimisticLockingFailureException;

import jakarta.persistence.OptimisticLockException;

import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public Product create(Product product) {
        if (product == null) throw new IllegalArgumentException("product cannot be null");
        if (product.getSku() == null || product.getSku().isBlank()) throw new IllegalArgumentException("sku is required");
        return productRepository.save(product);
    }

    @Override
    public Product findById(UUID id) {
        return productRepository.findById(id).orElseThrow(() -> new NotFoundException("Product not found: " + id));
    }

    @Override
    @Transactional
    public Product update(UUID id, Product product) {
        if (product == null) throw new IllegalArgumentException("product cannot be null");
        Product existing = findById(id);
        if (product.getName() != null) existing.setName(product.getName());
        if (product.getPriceCents() > 0) existing.setPriceCents(product.getPriceCents());
        return productRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Product existing = findById(id);
        productRepository.delete(existing);
    }

    @Override
    public void adjustStock(UUID id, int delta) {
        final int maxAttempts = 3;
        int attempt = 0;
        while (true) {
            try {
                attempt++;
                adjustStockOnce(id, delta);
                return;
            } catch (OptimisticLockingFailureException | OptimisticLockException ex) {
                if (attempt >= maxAttempts) {
                    throw ex;
                }
                try {
                    Thread.sleep(50L * attempt);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(ie);
                }
            }
        }
    }

    // perform a single attempt inside its own transaction so optimistic lock failures
    // cause only that attempt to rollback and can be retried
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void adjustStockOnce(UUID id, int delta) {
        Product p = findById(id);
        int newQty = p.getStockQuantity() + delta;
        if (newQty < 0) throw new InsufficientStockException("Insufficient stock for product " + id);
        p.setStockQuantity(newQty);
        productRepository.save(p);
    }
}
