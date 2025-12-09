package com.example.oms.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record PaymentRequest(@Min(1) long amountCents, @NotBlank String method) {}
