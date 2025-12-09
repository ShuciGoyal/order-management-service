package com.example.oms.dto.response;

import java.util.UUID;

public record ProductResponse(UUID id, String sku, String name, long priceCents, int stockQuantity) {}
