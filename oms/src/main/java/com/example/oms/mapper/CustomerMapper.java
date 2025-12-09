package com.example.oms.mapper;

import com.example.oms.dto.request.CreateCustomerRequest;
import com.example.oms.dto.response.CustomerResponse;
import com.example.oms.model.Customer;

import java.time.Instant;

public final class CustomerMapper {

    private CustomerMapper() {}

    public static Customer toEntity(CreateCustomerRequest req) {
        if (req == null) return null;
        var c = new Customer();
        c.setName(req.name());
        c.setEmail(req.email());
        c.setCreatedAt(Instant.now());
        return c;
    }

    public static CustomerResponse toResponse(Customer c) {
        if (c == null) return null;
        return new CustomerResponse(
                c.getId() == null ? null : c.getId(),
                c.getName(),
                c.getEmail(),
                c.getCreatedAt() == null ? Instant.EPOCH : c.getCreatedAt()
        );
    }
}
