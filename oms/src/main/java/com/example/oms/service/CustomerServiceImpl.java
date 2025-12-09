package com.example.oms.service;

import com.example.oms.model.Customer;
import com.example.oms.repository.CustomerRepository;
import com.example.oms.service.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional
    public Customer create(Customer customer) {
        if (customer == null) throw new IllegalArgumentException("customer cannot be null");
        if (customer.getEmail() == null || customer.getEmail().isBlank()) throw new IllegalArgumentException("email is required");
        return customerRepository.save(customer);
    }

    @Override
    public Customer findById(UUID id) {
        return customerRepository.findById(id).orElseThrow(() -> new NotFoundException("Customer not found: " + id));
    }

    @Override
    @Transactional
    public Customer update(UUID id, Customer customer) {
        if (customer == null) throw new IllegalArgumentException("customer cannot be null");
        Customer existing = findById(id);
        if (customer.getName() != null) existing.setName(customer.getName());
        if (customer.getEmail() != null && !customer.getEmail().isBlank()) existing.setEmail(customer.getEmail());
        return customerRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Customer existing = findById(id);
        customerRepository.delete(existing);
    }
}
