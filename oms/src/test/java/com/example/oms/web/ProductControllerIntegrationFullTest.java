package com.example.oms.web;

import com.example.oms.model.Product;
import com.example.oms.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ProductControllerIntegrationFullTest {

    @Autowired
    WebApplicationContext wac;

    MockMvc mvc;

    @Autowired
    ProductRepository productRepository;

    @BeforeEach
    void setup() {
        this.mvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    void create_and_get_endToEnd() throws Exception {
        var reqJson = "{\"sku\":\"INT-SKU-1\",\"name\":\"IntegrationWidget\",\"priceCents\":1999,\"stockQuantity\":10}";

        var createRes = mvc.perform(post("/api/v1/products").contentType(MediaType.APPLICATION_JSON).content(reqJson))
                .andExpect(status().isCreated())
                .andReturn();

        String location = createRes.getResponse().getHeader("Location");
        assertThat(location).isNotBlank();

        // extract id from location and ensure repository has the product
        String id = location.substring(location.lastIndexOf('/') + 1);
        Optional<Product> p = productRepository.findById(java.util.UUID.fromString(id));
        assertThat(p).isPresent();

        mvc.perform(get("/api/v1/products/" + id)).andExpect(status().isOk());
    }
}
