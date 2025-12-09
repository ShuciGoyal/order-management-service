package com.example.oms.dto.response;

import java.util.UUID;

public record OrderItemResponse(UUID id, UUID productId, int quantity, long priceCents) {}
