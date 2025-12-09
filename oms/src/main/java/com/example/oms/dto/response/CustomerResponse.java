package com.example.oms.dto.response;

import java.time.Instant;
import java.util.UUID;

public record CustomerResponse(UUID id, String name, String email, Instant createdAt) {}
