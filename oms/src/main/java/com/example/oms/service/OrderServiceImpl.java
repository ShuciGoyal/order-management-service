package com.example.oms.service;

import com.example.oms.model.Order;
import com.example.oms.repository.OrderRepository;
import com.example.oms.service.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.oms.model.OrderItem;
import com.example.oms.model.OrderStatus;

import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;

    public OrderServiceImpl(OrderRepository orderRepository, ProductService productService) {
        this.orderRepository = orderRepository;
        this.productService = productService;
    }

    @Override
    @Transactional
    public Order createOrder(Order order) {
    if (order == null) throw new IllegalArgumentException("order cannot be null");
    return orderRepository.save(order);
    }

    @Override
    public Order findById(UUID id) {
        return orderRepository.findById(id).orElseThrow(() -> new NotFoundException("Order not found: " + id));
    }

    @Override
    @Transactional
    public Order update(UUID id, Order order) {
        if (order == null) throw new IllegalArgumentException("order cannot be null");
        Order existing = findById(id);
        // minimal update: only update total if set
        if (order.getTotalCents() > 0) existing.setTotalCents(order.getTotalCents());
        if (order.getStatus() != null) existing.setStatus(order.getStatus());
        return orderRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Order existing = findById(id);
        orderRepository.delete(existing);
    }

    @Override
    @Transactional
    public Order placeOrder(UUID id) {
        Order order = findById(id);
        if (order.getStatus() == OrderStatus.PLACED || order.getStatus() == OrderStatus.PAID) {
            throw new IllegalStateException("Order already placed or paid: " + id);
        }

        // Decrement stock for each order item
        for (OrderItem item : order.getItems()) {
            var product = item.getProduct();
            if (product == null || product.getId() == null) {
                throw new IllegalArgumentException("Order item missing product reference");
            }
            // adjustStock will throw InsufficientStockException if not enough stock
            productService.adjustStock(product.getId(), -item.getQuantity());
        }

        order.setStatus(OrderStatus.PLACED);
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order cancelOrder(UUID id) {
        Order order = findById(id);
        if (order.getStatus() == com.example.oms.model.OrderStatus.SHIPPED) {
            throw new IllegalStateException("Cannot cancel shipped order: " + id);
        }

        // restore stock for items only if they were already placed or paid
        if (order.getStatus() == com.example.oms.model.OrderStatus.PLACED || order.getStatus() == com.example.oms.model.OrderStatus.PAID) {
            for (OrderItem item : order.getItems()) {
                productService.adjustStock(item.getProduct().getId(), item.getQuantity());
            }
        }

        order.setStatus(com.example.oms.model.OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }
}
