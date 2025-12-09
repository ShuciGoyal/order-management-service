package com.example.oms.service;

import com.example.oms.model.Order;
import java.util.UUID;

public interface OrderService {
    Order createOrder(Order order);
    Order findById(UUID id);
    Order update(UUID id, Order order);
    void delete(UUID id);
    Order placeOrder(UUID id);
    Order cancelOrder(UUID id);
}
