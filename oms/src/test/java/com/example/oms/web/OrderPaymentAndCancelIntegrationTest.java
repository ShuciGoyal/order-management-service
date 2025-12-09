package com.example.oms.web;

import com.example.oms.model.Product;
import com.example.oms.repository.ProductRepository;
import com.example.oms.repository.OrderRepository;
import com.example.oms.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class OrderPaymentAndCancelIntegrationTest {

    @Autowired WebApplicationContext wac;
    MockMvc mvc;

    @Autowired ProductRepository productRepository;
    @Autowired OrderRepository orderRepository;
    @Autowired PaymentRepository paymentRepository;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        paymentRepository.deleteAll();
        orderRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    void pay_and_cancel_happyPath() throws Exception {
        Product p = new Product();
        p.setSku("PAY-SKU-1");
        p.setName("PayWidget");
        p.setPriceCents(2000);
        p.setStockQuantity(10);
        productRepository.save(p);

        // create a customer and order (reuse create order endpoint)
        String custJson = "{\"name\":\"Cust\",\"email\":\"c@e.com\"}";
        var createCust = mvc.perform(post("/api/v1/customers").contentType(MediaType.APPLICATION_JSON).content(custJson)).andExpect(status().isCreated()).andReturn();
        String custLoc = createCust.getResponse().getHeader("Location");
        String custId = custLoc.substring(custLoc.lastIndexOf('/') + 1);

        String orderJson = String.format("{\"customerId\":\"%s\", \"items\":[{\"productId\":\"%s\",\"quantity\":1}]}", custId, p.getId());
        var createOrder = mvc.perform(post("/api/v1/orders").contentType(MediaType.APPLICATION_JSON).content(orderJson)).andExpect(status().isCreated()).andReturn();
        String orderLoc = createOrder.getResponse().getHeader("Location");
        String orderId = orderLoc.substring(orderLoc.lastIndexOf('/') + 1);

        // place
        mvc.perform(post("/api/v1/orders/" + orderId + "/place")).andExpect(status().isOk());

        // pay
        String payJson = "{\"amountCents\":2000, \"method\":\"card\"}";
        mvc.perform(post("/api/v1/orders/" + orderId + "/pay").contentType(MediaType.APPLICATION_JSON).content(payJson)).andExpect(status().isOk());

        // cancel should restore stock and set status to CANCELLED
        mvc.perform(post("/api/v1/orders/" + orderId + "/cancel")).andExpect(status().isOk());
    }
}
