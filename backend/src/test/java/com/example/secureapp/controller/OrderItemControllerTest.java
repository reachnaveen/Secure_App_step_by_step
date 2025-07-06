package com.example.secureapp.controller;

import com.example.secureapp.domain.OrderItem;
import com.example.secureapp.domain.Product;
import com.example.secureapp.repository.OrderItemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(OrderItemController.class)
public class OrderItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderItemRepository orderItemRepository;

    @Test
    public void testGetAllOrderItems() throws Exception {
        Product product = new Product();
        product.setId(1L);
        product.setName("Product 1");

        OrderItem orderItem1 = new OrderItem();
        orderItem1.setId(1L);
        orderItem1.setProduct(product);
        orderItem1.setQuantity(1);

        OrderItem orderItem2 = new OrderItem();
        orderItem2.setId(2L);
        orderItem2.setProduct(product);
        orderItem2.setQuantity(2);

        when(orderItemRepository.findAll()).thenReturn(Arrays.asList(orderItem1, orderItem2));

        mockMvc.perform(get("/api/order-items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].quantity").value(1))
                .andExpect(jsonPath("$[1].quantity").value(2));
    }
}
