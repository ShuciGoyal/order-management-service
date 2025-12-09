package com.example.oms.service;

import com.example.oms.model.Order;
import com.example.oms.model.Payment;
import com.example.oms.repository.OrderRepository;
import com.example.oms.repository.PaymentRepository;
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
import static org.mockito.Mockito.when;

class PaymentServiceTest {

    @Mock PaymentRepository paymentRepository;
    @Mock OrderRepository orderRepository;

    @InjectMocks PaymentServiceImpl paymentService;

    @BeforeEach void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void recordPayment_happyPath() {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(paymentRepository.save(org.mockito.ArgumentMatchers.any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        Payment p = paymentService.recordPayment(order.getId(), 1000L, "card");
        assertThat(p).isNotNull();
        assertThat(order.getStatus()).isEqualTo(com.example.oms.model.OrderStatus.PAID);
    }

    @Test
    void recordPayment_missingOrder() {
        UUID id = UUID.randomUUID();
        when(orderRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> paymentService.recordPayment(id, 1000L, "card")).isInstanceOf(NotFoundException.class);
    }
}
