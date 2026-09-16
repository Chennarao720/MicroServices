package com.microservices.paymentservice.service;
import com.microservices.paymentservice.dto.*;
import com.microservices.paymentservice.entity.Payment;
import com.microservices.paymentservice.entity.PaymentStatus;
import com.microservices.paymentservice.repository.PaymentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.microservices.paymentservice.entity.PaymentStatus.SUCCESS;

@Service
public class PaymentService {
  private final PaymentRepository paymentRepository;
  private final ModelMapper mapper;
  public PaymentService(PaymentRepository paymentRepository, ModelMapper mapper) {
    this.paymentRepository = paymentRepository;
      this.mapper = mapper;
  }
  public PaymentResponse makePayment(PaymentRequest request) {
    Payment map = mapper.map(request, Payment.class);
    map.setStatus(SUCCESS);
    Payment save = paymentRepository.save(map);
    PaymentResponse map1 = mapper.map(save, PaymentResponse.class);
    return map1;
  }

  //refund the payment when we cancel the order
  public PaymentResponse refundPayment(PaymentRequest request){
    Payment payment = paymentRepository.findByOrderId(request.getOrderId()).get();
    if (payment.getStatus() == PaymentStatus.SUCCESS) {
      payment.setStatus(PaymentStatus.REFUNDED);
      Payment save = paymentRepository.save(payment);
      return mapper.map(save,PaymentResponse.class);
    } else {

      throw new RuntimeException("Payment cannot be refunded");
    }
  }
}
