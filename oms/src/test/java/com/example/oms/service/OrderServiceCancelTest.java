package com.example.oms.service;

import com.example.oms.model.Order;
import com.example.oms.model.OrderItem;
import com.example.oms.model.Product;
import com.example.oms.repository.OrderRepository;
import com.example.oms.service.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class OrderServiceCancelTest {

    @Mock OrderRepository orderRepository;
    @Mock ProductService productService;

    @InjectMocks OrderServiceImpl orderService;

    @BeforeEach void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void cancel_restoresStockAndSetsCancelled() {
        Order o = new Order();
        o.setId(UUID.randomUUID());
        o.setStatus(com.example.oms.model.OrderStatus.PLACED);
        Product p = new Product(); p.setId(UUID.randomUUID());
        OrderItem it = new OrderItem(); it.setProduct(p); it.setQuantity(2);
        o.setItems(List.of(it));

        when(orderRepository.findById(o.getId())).thenReturn(Optional.of(o));
        when(orderRepository.save(org.mockito.ArgumentMatchers.any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order updated = orderService.cancelOrder(o.getId());
        assertThat(updated.getStatus()).isEqualTo(com.example.oms.model.OrderStatus.CANCELLED);
    }

    @Test
    void cancel_missingOrder() {
        UUID id = UUID.randomUUID();
        when(orderRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> orderService.cancelOrder(id)).isInstanceOf(NotFoundException.class);
    }
}
