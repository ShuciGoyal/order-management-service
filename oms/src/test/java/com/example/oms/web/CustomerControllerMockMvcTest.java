package com.example.oms.web;

import com.example.oms.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CustomerControllerMockMvcTest {

    MockMvc mvc;
    CustomerService svc;

    @BeforeEach
    void setup() {
        svc = Mockito.mock(CustomerService.class);
        var ctrl = new CustomerController(svc);
        mvc = MockMvcBuilders.standaloneSetup(ctrl)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void create_validationError() throws Exception {
        var badJson = "{\"name\":\"\",\"email\":\"not-an-email\"}";
        mvc.perform(post("/api/v1/customers").contentType(MediaType.APPLICATION_JSON).content(badJson))
                .andExpect(status().isUnprocessableEntity());
    }
}
