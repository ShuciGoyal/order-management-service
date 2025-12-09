package com.example.oms.dto.response;

import com.example.oms.model.OrderStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResponse(UUID id, UUID customerId, List<OrderItemResponse> items, OrderStatus status, Instant createdAt) {}
