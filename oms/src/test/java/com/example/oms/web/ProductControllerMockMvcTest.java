package com.example.oms.web;

import com.example.oms.model.Product;
import com.example.oms.service.ProductService;
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

class ProductControllerMockMvcTest {

    MockMvc mvc;
    ProductService productService;

    @BeforeEach
    void setup() {
        productService = Mockito.mock(ProductService.class);
        var controller = new ProductController(productService);
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void create_happyPath() throws Exception {
        var reqJson = "{\"sku\":\"SKU-1\",\"name\":\"Widget\",\"priceCents\":1999,\"stockQuantity\":10}";
        var p = new Product(); p.setId(UUID.randomUUID()); p.setSku("SKU-1");
        when(productService.create(any(Product.class))).thenReturn(p);

        mvc.perform(post("/api/v1/products").contentType(MediaType.APPLICATION_JSON).content(reqJson))
                .andExpect(status().isCreated());
    }

    @Test
    void get_404() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.when(productService.findById(id)).thenThrow(new com.example.oms.service.exception.NotFoundException("x"));
        mvc.perform(get("/api/v1/products/" + id)).andExpect(status().isNotFound());
    }

    @Test
    void create_validationError() throws Exception {
        var badJson = "{\"sku\":\"\",\"name\":\"\",\"priceCents\":-1,\"stockQuantity\":-5}";
        mvc.perform(post("/api/v1/products").contentType(MediaType.APPLICATION_JSON).content(badJson))
                .andExpect(status().isUnprocessableEntity());
    }
}
