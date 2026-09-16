package com.microservices.orderservice.controller;

import com.microservices.orderservice.dto.CancelResponse;
import com.microservices.orderservice.dto.OrderDto;
import com.microservices.orderservice.dto.PaymentDto;
import com.microservices.orderservice.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderDto> saveOrder(@RequestBody OrderDto orderDto) {
        OrderDto savedOrder = orderService.savePayment(orderDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);
    }

    @PostMapping("/refund/{id}")
    public ResponseEntity<CancelResponse> refund(@PathVariable Long id){
       CancelResponse cancelOrder = orderService.cancelOrder(id);
        return ResponseEntity.status(200).body(cancelOrder);
    }
}
