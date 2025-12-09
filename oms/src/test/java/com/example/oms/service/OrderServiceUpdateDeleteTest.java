package com.example.oms.service;

import com.example.oms.model.Order;
import com.example.oms.repository.OrderRepository;
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

class OrderServiceUpdateDeleteTest {

    @Mock
    OrderRepository orderRepository;

    @InjectMocks
    OrderServiceImpl orderService;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void updateExistingOrder() {
        Order o = new Order();
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));
        o.setId(UUID.randomUUID());
        o.setTotalCents(1000);

        when(orderRepository.findById(o.getId())).thenReturn(Optional.of(o));

        Order updated = new Order();
        updated.setTotalCents(2000);

        Order result = orderService.update(o.getId(), updated);
        assertThat(result.getTotalCents()).isEqualTo(2000);
    }

    @Test
    void updateMissingOrderThrows() {
        UUID id = UUID.randomUUID();
        when(orderRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> orderService.update(id, new Order())).isInstanceOf(NotFoundException.class);
    }

    @Test
    void deleteMissingOrderThrows() {
        UUID id = UUID.randomUUID();
        when(orderRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> orderService.delete(id)).isInstanceOf(NotFoundException.class);
    }
}
