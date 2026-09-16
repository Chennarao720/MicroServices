package com.microservices.orderservice.dto;

import lombok.Data;

@Data
public class ProductDto {

    private Long id;
    private String name;
    private double price;
    private int quantity;
}
