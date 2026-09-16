package com.microservices.paymentservice.entity;
import jakarta.persistence.*;
import lombok.Data;
@Entity @Table(name="payments") @Data
public class Payment {
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  private Long orderId;
  private double amount;
  @Enumerated(EnumType.STRING)
  private PaymentStatus status;
}
