package com.example.oms.service;

import com.example.oms.model.Customer;
import java.util.UUID;

public interface CustomerService {
    Customer create(Customer customer);
    Customer findById(UUID id);
    Customer update(UUID id, Customer customer);
    void delete(UUID id);
}
