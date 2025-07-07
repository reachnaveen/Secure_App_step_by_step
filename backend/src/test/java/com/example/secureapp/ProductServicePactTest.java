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

import java.util.Arrays;

@Provider("ProductServiceProvider")
@PactFolder("src/test/resources/pacts")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductServicePactTest {

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
        orderItemRepository.deleteAll();
        productRepository.deleteAll();

        // Directly insert data with specific IDs to match Pact expectations
        jdbcTemplate.update("INSERT INTO PRODUCT (ID, NAME, DESCRIPTION, PRICE) VALUES (?, ?, ?, ?)",
                1L, "Example Product", "Description for example product", 10.0);

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
