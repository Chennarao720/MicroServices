package com.microservices.paymentservice.dto;
import lombok.Data;
@Data
public class PaymentResponse {
    private Long paymentId;
    private Long orderId;
    private double amount;
    private String status; }
