package com.example.oms.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.oms.model.Customer;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CustomerRepositoryTest {

    @Autowired
    CustomerRepository customerRepository;

    @Test
    void saveAndFindByEmail() {
        Customer c = new Customer();
        c.setName("Test User");
        c.setEmail("test-user@example.com");
        customerRepository.save(c);

        Optional<Customer> found = customerRepository.findByEmail("test-user@example.com");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test User");
    }
}
