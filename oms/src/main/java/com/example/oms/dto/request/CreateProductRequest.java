package com.example.oms.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Simple request DTO for creating a product. Fields map closely to the JPA entity.
 */
public record CreateProductRequest(
	@NotBlank String sku,
	@NotBlank String name,
	@Min(0) long priceCents,
	@Min(0) int stockQuantity
) {}
