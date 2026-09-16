package com.microservices.productmicroservice.controller;

import com.microservices.productmicroservice.DTO.ProductRequest;
import com.microservices.productmicroservice.DTO.ProductResponse;
import com.microservices.productmicroservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
public class ProductController {
@Autowired
    private ProductService service;

@PostMapping("/save_product")
public ResponseEntity<ProductResponse> save(@RequestBody ProductRequest request){
    ProductResponse save = service.save(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(save);
 }

 @GetMapping("/findbyid/{id}")
    public ResponseEntity<ProductResponse> find(@PathVariable  Long id){
     ProductResponse response = service.findById(id);
     return ResponseEntity.status(HttpStatus.OK).body(response);
 }
}
