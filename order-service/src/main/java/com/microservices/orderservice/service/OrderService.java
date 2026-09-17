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
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Optional;

import static com.microservices.orderservice.entity.Status.CANCELLED;
import static com.microservices.orderservice.entity.Status.CONFIRMED;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;
    private final RestTemplate restTemplate;
    private final  WebClient   webClient;


    public OrderService(OrderRepository orderRepository, ModelMapper modelMapper, RestTemplate restTemplate,  WebClient.Builder webClient) {
        this.orderRepository = orderRepository;
        this.modelMapper = modelMapper;
        this.restTemplate = restTemplate;
        this.webClient =  webClient.build();;
    }

    public OrderDto saveOrder(OrderDto orderDto) {
        ResponseEntity<ProductDto> product =
                restTemplate.getForEntity("http://localhost:40567/product/findbyid/"
                                + orderDto.getProductId(),
                        ProductDto.class);
        if (product != null) {
            Order order = modelMapper.map(orderDto, Order.class);
            Order savedOrder = orderRepository.save(order);
            return modelMapper.map(savedOrder, OrderDto.class);
        } else {
            throw new RuntimeException();
        }
    }

    public OrderDto savePayment(OrderDto orderDto) {
        Order map = modelMapper.map(orderDto, Order.class);
        Order save = orderRepository.save(map);
        PaymentDto payment = new PaymentDto();
        payment.setOrderId(save.getId());
        payment.setAmount(orderDto.getPrice() * orderDto.getQuantity());
        payment.setStatus(orderDto.getStatus());
        ResponseEntity<PaymentDto> responseEntity =
                restTemplate.postForEntity(
                        "http://localhost:8083/payment/savePayment",
                        payment,
                        PaymentDto.class
                );
        PaymentDto paymentDto = responseEntity.getBody();
        if (paymentDto.getStatus().equals("SUCCESS")) {
            Order order = orderRepository.findById(paymentDto.getOrderId()).get();
            order.setStatus(CONFIRMED);
            Order save1 = orderRepository.save(order);
            OrderDto map1 = modelMapper.map(save1, OrderDto.class);
            return map1;
        } else {
            throw new RuntimeException("Payment Failed");
        }
    }

    //we have alredy this methods i.e in place of invontary we used product\
    public OrderDto fetchOrderById(Long id) {
        Order order = orderRepository.findById(id).get();
        return modelMapper.map(order, OrderDto.class);
    }


//    This one is using the RestTemplet
    //Cancel the order and refund the amount
//    public CancelResponse cancelOrder(Long orderId){
//        Order byId = orderRepository.findById(orderId).orElseThrow(()->{
//            throw new RuntimeException("No orderfound");
//        });
//        PaymentDto payment=new PaymentDto();
//        payment.setOrderId(byId.getId());
//        HttpEntity<PaymentDto> request =
//                new HttpEntity<>(payment);
//        ResponseEntity<PaymentDto> exchange = restTemplate.exchange(
//                "http://PAYMENT-SERVICE/payment/refundPayment",
//                HttpMethod.POST,
//                request, PaymentDto.class);
//        byId.setStatus(CANCELLED);
//        Order save = orderRepository.save(byId);
//        OrderDto map1 = modelMapper.map(save, OrderDto.class);
//        PaymentDto map = modelMapper.map(exchange.getBody(), PaymentDto.class);
//        CancelResponse cancel=new CancelResponse();
//        cancel.setOrderdto(map1);
//        cancel.setPaymentdto(map);
//        return cancel;
//    }

//    This one using the webClient
//public ResponseEntity<Mono<CancelResponse>> canceling(Long id) {
//
//    Order order = orderRepository.findById(id).get();
//
//    PaymentDto paymentDto = new PaymentDto();
//    paymentDto.setOrderId(order.getId());
//
//    System.out.println(
//            "Before Payment call - " +
//                    Thread.currentThread().getName()
//    );
//
//    long start = System.currentTimeMillis();
//
//    Mono<PaymentDto> paymentDtoMono = webClient
//            .post()
//            .uri("http://PAYMENT-SERVICE/payment/refundPayment")
//            .bodyValue(paymentDto)
//            .retrieve()
//            .bodyToMono(PaymentDto.class);
//
//    long monoCreated = System.currentTimeMillis();
//
//    System.out.println(
//            "After creating Mono - " +
//                    Thread.currentThread().getName()
//    );
//
//    System.out.println(
//            "Time taken to create Mono = " +
//                    (monoCreated - start) +
//                    " ms"
//    );
//
//    order.setStatus(CANCELLED);
//
//    Order save = orderRepository.save(order);
//
//    OrderDto map = modelMapper.map(save, OrderDto.class);
//
//    Mono<CancelResponse> map1 = paymentDtoMono.map(payment -> {
//
//        long end = System.currentTimeMillis();
//
//        System.out.println(
//                "Payment response received - " +
//                        Thread.currentThread().getName()
//        );
//
//        System.out.println(
//                "Total time until Payment response = " +
//                        (end - start) +
//                        " ms"
//        );
//
//        CancelResponse response = new CancelResponse();
//        response.setPaymentdto(payment);
//        response.setOrderdto(map);
//
//        return response;
//    });
//
//    return ResponseEntity.ok(map1);
//}


//    WebClient using blocking mechanism
    public ResponseEntity<Mono<CancelResponse>> canceling(Long id) {

        Order order = orderRepository.findById(id).get();

        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setOrderId(order.getId());

        System.out.println(
                "Before Payment call - " +
                        Thread.currentThread().getName()
        );

        Mono<PaymentDto> paymentMono = Mono.fromCallable(() -> {
            long start = System.currentTimeMillis();

            System.out.println(
                    "Before block - " +
                            Thread.currentThread().getName()
            );

            PaymentDto payment = webClient
                    .post()
                    .uri("http://PAYMENT-SERVICE/payment/refundPayment")
                    .bodyValue(paymentDto)
                    .retrieve()
                    .bodyToMono(PaymentDto.class)
                    .block();
            long end = System.currentTimeMillis();
            System.out.println(
                    "After block - " +
                            Thread.currentThread().getName()
            );
            System.out.println(
                    "Blocking time = " +
                            (end - start) +
                            " ms"
            );

            return payment;

        }).subscribeOn(Schedulers.boundedElastic());

        System.out.println(
                "After creating Mono - " +
                        Thread.currentThread().getName()
        );

        order.setStatus(CANCELLED);

        Order save = orderRepository.save(order);

        OrderDto map = modelMapper.map(save, OrderDto.class);

        Mono<CancelResponse> responseMono = paymentMono.map(payment -> {

            CancelResponse response = new CancelResponse();

            response.setPaymentdto(payment);
            response.setOrderdto(map);

            return response;
        });

        return ResponseEntity.ok(responseMono);
    }
}
