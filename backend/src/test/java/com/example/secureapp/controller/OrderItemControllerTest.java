package com.example.secureapp.controller;

import com.example.secureapp.domain.OrderItem;
import com.example.secureapp.domain.Product;
import com.example.secureapp.repository.OrderItemRepository;
import com.example.secureapp.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@ActiveProfiles("integrationtest")
public class OrderItemControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    private Product testProduct;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
        orderItemRepository.deleteAll();
        productRepository.deleteAll();

        testProduct = new Product();
        testProduct.setName("Test Product");
        testProduct.setDescription("Description for test product");
        testProduct.setPrice(99.99);
        testProduct = productRepository.save(testProduct);
    }

    @AfterEach
    public void teardown() {
        orderItemRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    public void testGetAllOrderItems() throws Exception {
        OrderItem orderItem1 = new OrderItem();
        orderItem1.setProduct(testProduct);
        orderItem1.setQuantity(1);
        orderItemRepository.save(orderItem1);

        OrderItem orderItem2 = new OrderItem();
        orderItem2.setProduct(testProduct);
        orderItem2.setQuantity(2);
        orderItemRepository.save(orderItem2);

        mockMvc.perform(get("/api/order-items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].quantity").value(1))
                .andExpect(jsonPath("$[1].quantity").value(2));
    }

    @Test
    public void testCreateOrderItem() throws Exception {
        String orderItemJson = String.format("{\"product\":{\"id\":%d},\"quantity\":5}", testProduct.getId());

        mockMvc.perform(post("/api/order-items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderItemJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(5));
    }
}