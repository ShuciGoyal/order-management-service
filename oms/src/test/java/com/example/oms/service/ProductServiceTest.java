package com.example.oms.service;

import com.example.oms.model.Product;
import com.example.oms.repository.ProductRepository;
import com.example.oms.service.exception.InsufficientStockException;
import com.example.oms.service.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ProductServiceTest {

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createAndFind() {
        Product p = new Product();
        p.setSku("SKU-1");
        p.setName("Sample");
        p.setPriceCents(1000);

        when(productRepository.save(any(Product.class))).thenAnswer(inv -> {
            Product arg = inv.getArgument(0);
            arg.setId(UUID.randomUUID());
            return arg;
        });

        Product saved = productService.create(p);
        assertThat(saved.getId()).isNotNull();

        when(productRepository.findById(saved.getId())).thenReturn(Optional.of(saved));
        Product found = productService.findById(saved.getId());
        assertThat(found.getSku()).isEqualTo("SKU-1");
    }

    @Test
    void adjustStock_insufficient() {
        Product p = new Product();
        p.setId(UUID.randomUUID());
        p.setStockQuantity(1);

        when(productRepository.findById(p.getId())).thenReturn(Optional.of(p));

        assertThatThrownBy(() -> productService.adjustStock(p.getId(), -2))
            .isInstanceOf(InsufficientStockException.class);
    }

    @Test
    void find_missing() {
        UUID id = UUID.randomUUID();
        when(productRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> productService.findById(id)).isInstanceOf(NotFoundException.class);
    }
}
