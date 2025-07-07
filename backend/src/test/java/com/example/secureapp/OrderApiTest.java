package com.example.secureapp;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderApiTest {

    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    @Test
    public void testCreateAndGetOrder() {
        // Create a new order
        String requestBody = "{\"customerName\":\"Test Customer\", \"orderItems\":[]}";

        Long orderId = ((Integer) given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/api/orders")
        .then()
            .statusCode(200)
            .body("customerName", equalTo("Test Customer"))
            .body("id", notNullValue())
            .extract().path("id")).longValue();

        // Get all orders and verify the new order is present
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/api/orders")
        .then()
            .statusCode(200)
            .body("customerName", hasItems("Test Customer"));
    }
}
