package com.microservices.orderservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CancelResponse {
    PaymentDto paymentdto;
    OrderDto orderdto;
}
