package com.example.oms.web;

import com.example.oms.dto.request.CreateCustomerRequest;
import com.example.oms.model.Customer;
import com.example.oms.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerControllerTest {

    @Test
    void createAndGet() {
        var svc = Mockito.mock(CustomerService.class);
        var ctrl = new CustomerController(svc);

        var req = new CreateCustomerRequest("Alice", "alice@example.com");
        var saved = new Customer();
        UUID id = UUID.randomUUID(); saved.setId(id); saved.setName(req.name()); saved.setEmail(req.email());
        Mockito.when(svc.create(Mockito.any(Customer.class))).thenReturn(saved);
        var resp = ctrl.create(req);
        assertThat(resp.getStatusCode().value()).isEqualTo(201);

        Mockito.when(svc.findById(id)).thenReturn(saved);
        var get = ctrl.get(id);
        assertThat(get.getStatusCode().value()).isEqualTo(200);
    }
}
