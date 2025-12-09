package com.example.oms.web;

import com.example.oms.dto.request.CreateOrderRequest;
import com.example.oms.dto.request.OrderItemRequest;
import com.example.oms.model.Product;
import com.example.oms.repository.ProductRepository;
import com.example.oms.repository.OrderRepository;
import com.example.oms.dto.response.OrderResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class OrderPlacementIntegrationTest {

    @Autowired WebApplicationContext wac;
    MockMvc mvc;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    OrderRepository orderRepository;
    
    @Autowired
    com.example.oms.repository.PaymentRepository paymentRepository;

    @Autowired
    com.example.oms.repository.CustomerRepository customerRepository;

    @BeforeEach
    void setup() {
    mvc = MockMvcBuilders.webAppContextSetup(wac).build();
    // delete dependent tables in correct order to avoid FK constraints
    paymentRepository.deleteAll();
    orderRepository.deleteAll();
    productRepository.deleteAll();
    customerRepository.deleteAll();
    }

    @Test
    void place_happyPath() throws Exception {
        Product p = new Product();
        p.setSku("PLACED-SKU-1");
        p.setName("PlacedWidget");
        p.setPriceCents(1000);
        p.setStockQuantity(5);
        productRepository.save(p);

    // create and persist a customer so the order can reference it
    com.example.oms.model.Customer cust = new com.example.oms.model.Customer();
    cust.setName("Test Customer");
    cust.setEmail("test1@example.com");
    customerRepository.save(cust);

        // create order JSON referencing the product id
    String orderJson = String.format("{\"customerId\":\"%s\", \"items\":[{\"productId\":\"%s\",\"quantity\":2}]}", cust.getId(), p.getId());

        var create = mvc.perform(post("/api/v1/orders").contentType(MediaType.APPLICATION_JSON).content(orderJson))
                .andExpect(status().isCreated())
                .andReturn();

        String location = create.getResponse().getHeader("Location");
        assertThat(location).isNotBlank();
        String id = location.substring(location.lastIndexOf('/') + 1);

        // place order
        mvc.perform(post("/api/v1/orders/" + id + "/place")).andExpect(status().isOk());

        // verify product stock decreased
        Product updated = productRepository.findById(p.getId()).orElseThrow();
        assertThat(updated.getStockQuantity()).isEqualTo(3);
    }

    @Test
    void place_insufficientStock_returns409() throws Exception {
        Product p = new Product();
        p.setSku("PLACED-SKU-2");
        p.setName("PlacedWidget2");
        p.setPriceCents(1000);
        p.setStockQuantity(1);
        productRepository.save(p);

    // create and persist a customer so the order can reference it
    com.example.oms.model.Customer cust = new com.example.oms.model.Customer();
    cust.setName("Test Customer 2");
    cust.setEmail("test2@example.com");
    customerRepository.save(cust);

    String orderJson = String.format("{\"customerId\":\"%s\", \"items\":[{\"productId\":\"%s\",\"quantity\":2}]}", cust.getId(), p.getId());

        var create = mvc.perform(post("/api/v1/orders").contentType(MediaType.APPLICATION_JSON).content(orderJson))
                .andExpect(status().isCreated())
                .andReturn();

        String location = create.getResponse().getHeader("Location");
        String id = location.substring(location.lastIndexOf('/') + 1);

        mvc.perform(post("/api/v1/orders/" + id + "/place")).andExpect(status().isConflict());

        // product should be unchanged
        Product updated = productRepository.findById(p.getId()).orElseThrow();
        assertThat(updated.getStockQuantity()).isEqualTo(1);
    }
}
