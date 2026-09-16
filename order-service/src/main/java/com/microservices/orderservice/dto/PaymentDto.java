package com.microservices.orderservice.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class PaymentDto {
    private Long orderId;
    private double amount;
    private String status;
}
