package com.example.secureapp;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderItemApiTest {

    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    @Test
    public void testCreateOrderItem() {
        // First, create a product to associate with the order item
        String productRequestBody = "{\"name\":\"Test Product for OrderItem\", \"description\":\"Description\", \"price\":15.50}";
        Long productId = ((Integer) given()
            .contentType(ContentType.JSON)
            .body(productRequestBody)
        .when()
            .post("/api/products")
        .then()
            .statusCode(200)
            .extract().path("id")).longValue();

        // Create an order item
        String orderItemRequestBody = String.format("{\"product\":{\"id\":%d},\"quantity\":2}", productId);

        given()
            .contentType(ContentType.JSON)
            .body(orderItemRequestBody)
        .when()
            .post("/api/order-items")
        .then()
            .statusCode(200)
            .body("quantity", equalTo(2))
            .body("product.id", equalTo(productId.intValue()))
            .body("id", notNullValue());
    }
}