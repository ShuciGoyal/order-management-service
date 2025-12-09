package com.example.oms.web;

import com.example.oms.dto.request.CreateCustomerRequest;
import com.example.oms.dto.response.CustomerResponse;
import com.example.oms.mapper.CustomerMapper;
import com.example.oms.model.Customer;
import com.example.oms.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> create(@RequestBody @Valid CreateCustomerRequest req) {
        Customer c = CustomerMapper.toEntity(req);
        var saved = customerService.create(c);
        var resp = CustomerMapper.toResponse(saved);
        return ResponseEntity.created(URI.create("/api/v1/customers/" + saved.getId())).body(resp);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> get(@PathVariable UUID id) {
        var c = customerService.findById(id);
        return ResponseEntity.ok(CustomerMapper.toResponse(c));
    }
}
