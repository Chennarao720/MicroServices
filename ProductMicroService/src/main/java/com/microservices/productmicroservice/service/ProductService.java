package com.microservices.productmicroservice.service;


import com.microservices.productmicroservice.DTO.ProductRequest;
import com.microservices.productmicroservice.DTO.ProductResponse;
import com.microservices.productmicroservice.entity.Product;
import com.microservices.productmicroservice.repository.ProductRepo;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    private final ProductRepo repo;
    private final ModelMapper mapper;
    
    ProductService(ProductRepo repo,ModelMapper mapper){
        this.repo=repo;
        this.mapper=mapper;
    }

    //for saving product
    public ProductResponse save(ProductRequest request){
        Product product = mapper.map(request, Product.class);
        Product save = repo.save(product);
        ProductResponse map = mapper.map(save, ProductResponse.class);
        return map;
    }

    //find by id
    public ProductResponse findById(Long id){
        Product product = repo.findById(id).orElseThrow(() -> {
            throw new RuntimeException("No product found");
        });
        ProductResponse response = mapper.map(product, ProductResponse.class);
        return response;
    }
}

