package com.microservices.productmicroservice.DTO;


import lombok.Data;

@Data
public class ProductRequest {
    private Long id;
    private String name;
    private Double price;
    private Integer quantity;
}

