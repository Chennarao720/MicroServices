package com.microservices.orderservice.dto;

import lombok.Data;

@Data
public class OrderDto {

    private Long productId;
    private int quantity;
    private double price;
    private String status;
}
