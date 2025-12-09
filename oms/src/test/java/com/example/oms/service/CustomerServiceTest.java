package com.example.oms.service;

import com.example.oms.model.Customer;
import com.example.oms.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CustomerServiceTest {

    @Mock
    CustomerRepository customerRepository;

    @InjectMocks
    CustomerServiceImpl customerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createAndFind() {
        Customer c = new Customer();
        c.setName("Test User");
        c.setEmail("test-user@example.com");

        when(customerRepository.save(any(Customer.class))).thenAnswer(inv -> {
            Customer arg = inv.getArgument(0);
            arg.setId(UUID.randomUUID());
            return arg;
        });

        Customer saved = customerService.create(c);
        assertThat(saved.getId()).isNotNull();

        when(customerRepository.findById(saved.getId())).thenReturn(Optional.of(saved));
        Customer found = customerService.findById(saved.getId());
        assertThat(found.getEmail()).isEqualTo("test-user@example.com");
    }
}
