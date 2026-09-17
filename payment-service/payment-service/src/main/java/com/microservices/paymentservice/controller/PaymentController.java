package com.microservices.paymentservice.controller;
import com.microservices.paymentservice.dto.*;
import com.microservices.paymentservice.service.PaymentService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/payment")
public class PaymentController {
  private final PaymentService paymentService;
  public PaymentController(PaymentService paymentService) { this.paymentService = paymentService; }
  @PostMapping("/savePayment")
  public ResponseEntity<PaymentResponse> makePayment(@RequestBody PaymentRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.makePayment(request));
  }

  @PostMapping("/refundPayment")
  public ResponseEntity<PaymentResponse> refund(@RequestBody PaymentRequest request) throws InterruptedException {
    System.out.println("Payment Service - START");

    Thread.sleep(5000);

    System.out.println("Payment Service - END");

    return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.refundPayment(request));
  }
}
