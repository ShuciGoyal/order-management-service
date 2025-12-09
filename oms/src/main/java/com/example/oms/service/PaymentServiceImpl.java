package com.example.oms.service;

import com.example.oms.model.Payment;
import com.example.oms.model.Order;
import com.example.oms.repository.PaymentRepository;
import com.example.oms.repository.OrderRepository;
import com.example.oms.service.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository, OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public Payment recordPayment(UUID orderId, long amountCents, String method) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException("Order not found: " + orderId));
        Payment p = new Payment();
        p.setOrder(order);
        p.setAmountCents(amountCents);
        p.setMethod(method);
        p.setStatus("COMPLETED");
        Payment saved = paymentRepository.save(p);

        order.setStatus(com.example.oms.model.OrderStatus.PAID);
        orderRepository.save(order);

        return saved;
    }
}
