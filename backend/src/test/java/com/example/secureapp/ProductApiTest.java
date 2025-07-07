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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductApiTest {

    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    @Test
    public void testCreateAndGetProduct() {
        // Create a new product
        String requestBody = "{\"name\":\"Test Product\", \"description\":\"A product for testing\", \"price\":10.99}";

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/api/products")
        .then()
            .statusCode(200)
            .body("name", equalTo("Test Product"))
            .body("description", equalTo("A product for testing"))
            .body("price", equalTo(10.99f));

        // Get all products and verify the new product is present
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/api/products")
        .then()
            .statusCode(200)
            .body("name", hasItems("Test Product"));
    }
}
