package com.example.oms.web;

import com.example.oms.model.Order;
import com.example.oms.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderControllerMockMvcTest {

    MockMvc mvc;
    OrderService orderService;

    @BeforeEach
    void setup() {
        orderService = Mockito.mock(OrderService.class);
        var controller = new OrderController(orderService);
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void create_happyPath() throws Exception {
        UUID cust = UUID.randomUUID();
        UUID prodId = UUID.randomUUID();
        var reqJson = String.format("{\"customerId\":\"%s\",\"items\":[{\"productId\":\"%s\",\"quantity\":1}]}", cust, prodId);
    var saved = new Order(); saved.setId(UUID.randomUUID());
    var custObj = new com.example.oms.model.Customer(); custObj.setId(cust);
    saved.setCustomer(custObj);
        when(orderService.createOrder(any(Order.class))).thenReturn(saved);

        mvc.perform(post("/api/v1/orders").contentType(MediaType.APPLICATION_JSON).content(reqJson))
                .andExpect(status().isCreated());
    }

    @Test
    void get_404() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.when(orderService.findById(id)).thenThrow(new com.example.oms.service.exception.NotFoundException("x"));
        mvc.perform(get("/api/v1/orders/" + id)).andExpect(status().isNotFound());
    }

    @Test
    void create_validationError() throws Exception {
        var badJson = "{\"customerId\":null,\"items\":[]}";
        mvc.perform(post("/api/v1/orders").contentType(MediaType.APPLICATION_JSON).content(badJson))
                .andExpect(status().isUnprocessableEntity());
    }
}
