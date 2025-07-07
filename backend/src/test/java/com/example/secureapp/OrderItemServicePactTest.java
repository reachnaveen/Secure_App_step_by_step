package com.example.secureapp;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import com.example.secureapp.domain.Product;
import com.example.secureapp.repository.OrderItemRepository;
import com.example.secureapp.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;

@Provider("OrderItemServiceProvider")
@PactFolder("src/test/resources/pacts")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderItemServicePactTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void before(PactVerificationContext context) {
        // Clear existing data and reset identity columns
        jdbcTemplate.execute("TRUNCATE TABLE ORDER_ITEM RESTART IDENTITY");
        jdbcTemplate.execute("TRUNCATE TABLE PRODUCT RESTART IDENTITY");

        // Populate with data expected by the Pact contract
        // Need to create a product first for the order item to reference
        jdbcTemplate.update("INSERT INTO PRODUCT (ID, NAME, DESCRIPTION, PRICE) VALUES (?, ?, ?, ?)",
                1L, "Test Product for OrderItem", "Description for test product", 15.50);

        context.setTarget(new HttpTestTarget("localhost", port));
    }

    @AfterEach
    void tearDown() {
        // Clear data in correct order after each test
        orderItemRepository.deleteAll();
        productRepository.deleteAll();
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }
}
