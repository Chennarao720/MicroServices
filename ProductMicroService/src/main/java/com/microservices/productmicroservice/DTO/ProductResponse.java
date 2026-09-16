package com.microservices.productmicroservice.DTO;

import lombok.Data;

@Data
public class ProductResponse {
    private Long id;
    private String name;
    private Double price;
    private Integer quantity;
}

