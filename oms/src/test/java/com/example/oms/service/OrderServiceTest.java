package com.example.oms.service;

import com.example.oms.model.Order;
import com.example.oms.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class OrderServiceTest {

    @Mock
    OrderRepository orderRepository;

    @InjectMocks
    OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createAndFindOrder() {
        Order o = new Order();

        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order arg = inv.getArgument(0);
            arg.setId(UUID.randomUUID());
            return arg;
        });

        Order saved = orderService.createOrder(o);
        assertThat(saved.getId()).isNotNull();

        when(orderRepository.findById(saved.getId())).thenReturn(Optional.of(saved));
        Order found = orderService.findById(saved.getId());
        assertThat(found).isNotNull();
    }
}
