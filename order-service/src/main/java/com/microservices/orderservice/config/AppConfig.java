package com.microservices.orderservice.config;

import com.microservices.orderservice.dto.OrderDto;
import com.microservices.orderservice.entity.Order;
import org.modelmapper.ModelMapper;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.typeMap(OrderDto.class, Order.class)
                .addMappings(m -> m.skip(Order::setId));
            return mapper;
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
