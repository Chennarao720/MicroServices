package com.microservices.paymentservice.config;

import com.microservices.paymentservice.dto.PaymentRequest;
import com.microservices.paymentservice.entity.Payment;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
    @Bean
    public ModelMapper mapper(){
        ModelMapper mapper=new ModelMapper();
        mapper.typeMap(PaymentRequest.class,Payment.class)
                .addMappings(m->m.skip(Payment::setId));
                return mapper;
    }
}
