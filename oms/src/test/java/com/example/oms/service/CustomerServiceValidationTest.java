package com.example.oms.service;

import com.example.oms.model.Customer;
import com.example.oms.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class CustomerServiceValidationTest {

    @Mock
    CustomerRepository customerRepository;

    @InjectMocks
    CustomerServiceImpl customerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_null_throws() {
        assertThatThrownBy(() -> customerService.create(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void create_missingEmail_throws() {
        Customer c = new Customer();
        c.setName("No Email");
        assertThatThrownBy(() -> customerService.create(c)).isInstanceOf(IllegalArgumentException.class);
    }
}
