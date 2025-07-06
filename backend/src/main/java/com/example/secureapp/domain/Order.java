package com.example.secureapp.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String customerName;
    @OneToMany(cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;
}
