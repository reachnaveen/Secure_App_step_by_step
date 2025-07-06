package com.example.secureapp.controller;

import com.example.secureapp.domain.Order;
import com.example.secureapp.repository.OrderRepository;
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
public class OrderControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
        orderRepository.deleteAll(); // Clear data before each test
    }

    @AfterEach
    public void teardown() {
        orderRepository.deleteAll(); // Clear data after each test
    }

    @Test
    public void testGetAllOrders() throws Exception {
        Order order1 = new Order();
        order1.setCustomerName("Customer 1");
        orderRepository.save(order1);

        Order order2 = new Order();
        order2.setCustomerName("Customer 2");
        orderRepository.save(order2);

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerName").value("Customer 1"))
                .andExpect(jsonPath("$[1].customerName").value("Customer 2"));
    }

    @Test
    public void testCreateOrder() throws Exception {
        String orderJson = "{\"customerName\":\"New Customer\", \"orderItems\":[]}";

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("New Customer"));
    }
}