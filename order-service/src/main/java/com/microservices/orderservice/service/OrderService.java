package com.microservices.orderservice.service;

import com.microservices.orderservice.dto.CancelResponse;
import com.microservices.orderservice.dto.OrderDto;
import com.microservices.orderservice.dto.PaymentDto;
import com.microservices.orderservice.dto.ProductDto;
import com.microservices.orderservice.entity.Order;
import com.microservices.orderservice.entity.Status;
import com.microservices.orderservice.repository.OrderRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static com.microservices.orderservice.entity.Status.CANCELLED;
import static com.microservices.orderservice.entity.Status.CONFIRMED;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;
    private final RestTemplate restTemplate;


    public OrderService(OrderRepository orderRepository, ModelMapper modelMapper, RestTemplate restTemplate) {
        this.orderRepository = orderRepository;
        this.modelMapper = modelMapper;
        this.restTemplate = restTemplate;
    }
    public OrderDto saveOrder(OrderDto orderDto) {
        ResponseEntity<ProductDto> product =
                restTemplate.getForEntity("http://localhost:40567/product/findbyid/"
                                + orderDto.getProductId(),
                        ProductDto.class);
        if (product != null) {
            Order order = modelMapper.map(orderDto,Order.class);
            Order savedOrder = orderRepository.save(order);
            return modelMapper.map(savedOrder,OrderDto.class);
        } else {
            throw new RuntimeException();
        }
    }
    public OrderDto savePayment(OrderDto orderDto) {
        Order map = modelMapper.map(orderDto, Order.class);
        Order save = orderRepository.save(map);
        PaymentDto  payment=new PaymentDto();
        payment.setOrderId(save.getId());
        payment.setAmount(orderDto.getPrice()*orderDto.getQuantity());
        payment.setStatus(orderDto.getStatus());
        ResponseEntity<PaymentDto> responseEntity =
                restTemplate.postForEntity(
                        "http://localhost:8083/payment/savePayment",
                        payment,
                        PaymentDto.class
                );
        PaymentDto paymentDto = responseEntity.getBody();
        if(paymentDto.getStatus().equals("SUCCESS")){
            Order order=  orderRepository.findById(paymentDto.getOrderId()).get();
            order.setStatus(CONFIRMED);
            Order save1 = orderRepository.save(order);
            OrderDto map1 = modelMapper.map(save1, OrderDto.class);
            return map1;
        }else{
            throw new RuntimeException("Payment Failed");
        }
    }

//we have alredy this methods i.e in place of invontary we used product\
    public OrderDto fetchOrderById(Long id){
        Order order = orderRepository.findById(id).get();
        return modelMapper.map(order,OrderDto.class);
    }

    //Cancel the order and refund the amount
    public CancelResponse cancelOrder(Long orderId){
        Order byId = orderRepository.findById(orderId).orElseThrow(()->{
            throw new RuntimeException("No orderfound");
        });
        PaymentDto payment=new PaymentDto();
        payment.setOrderId(byId.getId());
        HttpEntity<PaymentDto> request =
                new HttpEntity<>(payment);
        ResponseEntity<PaymentDto> exchange = restTemplate.exchange(
                "http://PAYMENT-SERVICE/payment/refundPayment",
                HttpMethod.POST,
                request, PaymentDto.class);
        byId.setStatus(CANCELLED);
        Order save = orderRepository.save(byId);
        OrderDto map1 = modelMapper.map(save, OrderDto.class);
        PaymentDto map = modelMapper.map(exchange.getBody(), PaymentDto.class);
        CancelResponse cancel=new CancelResponse();
        cancel.setOrderdto(map1);
        cancel.setPaymentdto(map);
        return cancel;
    }
}
