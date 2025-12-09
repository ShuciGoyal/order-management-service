package com.example.oms.repository;

import com.example.oms.model.Order;
import com.example.oms.model.OrderStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByCustomerId(UUID customerId);
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
}
