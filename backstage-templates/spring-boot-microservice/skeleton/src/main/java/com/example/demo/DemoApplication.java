package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ${{ values.component_id | capitalize }}Application {

    public static void main(String[] args) {
        SpringApplication.run(${{ values.component_id | capitalize }}Application.class, args);
    }

    @GetMapping("/")
    public String hello() {
        return "Hello from ${{ values.component_id }}!";
    }
}
