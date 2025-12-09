package com.example.oms.mapper;

import com.example.oms.dto.request.CreateCustomerRequest;
 
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerMapperTest {

    @Test
    void toEntity_and_toResponse_roundtrip() {
        var req = new CreateCustomerRequest("Alice", "alice@example.com");
        var entity = CustomerMapper.toEntity(req);
        // some fields set by mapper
        assertThat(entity.getName()).isEqualTo("Alice");
        assertThat(entity.getEmail()).isEqualTo("alice@example.com");

        entity.setCreatedAt(Instant.parse("2020-01-01T00:00:00Z"));
        var resp = CustomerMapper.toResponse(entity);
        assertThat(resp.name()).isEqualTo("Alice");
        assertThat(resp.email()).isEqualTo("alice@example.com");
        assertThat(resp.createdAt()).isEqualTo(Instant.parse("2020-01-01T00:00:00Z"));
    }
}
