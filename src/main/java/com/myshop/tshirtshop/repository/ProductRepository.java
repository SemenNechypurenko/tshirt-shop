package com.myshop.tshirtshop.repository;

import com.myshop.tshirtshop.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
}