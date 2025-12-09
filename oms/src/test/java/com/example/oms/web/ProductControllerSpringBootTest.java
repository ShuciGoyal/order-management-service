package com.example.oms.web;

import com.example.oms.model.Product;
import com.example.oms.repository.ProductRepository;
import com.example.oms.OmsApplication;
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

@SpringBootTest(classes = OmsApplication.class)
class ProductControllerSpringBootTest {

    @Autowired WebApplicationContext ctx;
    @Autowired ProductRepository productRepository;
    MockMvc mvc;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(ctx).build();
    }

    @Test
    void create_and_get_e2e() throws Exception {
        var reqJson = "{\"sku\":\"E2E-SKU-1\",\"name\":\"E2E Widget\",\"priceCents\":1500,\"stockQuantity\":3}";

        var res = mvc.perform(post("/api/v1/products").contentType(MediaType.APPLICATION_JSON).content(reqJson))
                .andExpect(status().isCreated()).andReturn();

        String location = res.getResponse().getHeader("Location");
        assertThat(location).isNotBlank();
        String id = location.substring(location.lastIndexOf('/') + 1);

        Optional<Product> p = productRepository.findById(java.util.UUID.fromString(id));
        assertThat(p).isPresent();

        mvc.perform(get("/api/v1/products/" + id)).andExpect(status().isOk());
    }
}
