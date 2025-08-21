package com.myshop.tshirtshop.service;

import com.myshop.tshirtshop.exception.ResourceNotFoundException;
import com.myshop.tshirtshop.model.Product;
import com.myshop.tshirtshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product createProduct(Product product) {
        log.debug("Creating product: {}", product.getName());

        if (product.getName() == null || product.getName().isEmpty()) {
            log.warn("Product creation failed: Name is required");
            throw new IllegalArgumentException("Product name is required");
        }
        if (product.getPrice() < 0) {
            log.warn("Product creation failed: Price cannot be negative");
            throw new IllegalArgumentException("Price cannot be negative");
        }
        if (product.getSizes() == null || product.getSizes().isEmpty()) {
            log.warn("Product creation failed: At least one size is required");
            throw new IllegalArgumentException("At least one size is required");
        }

        product.setCreatedAt(LocalDateTime.now());
        Product savedProduct = productRepository.save(product);
        log.info("Product created successfully: {}", savedProduct.getId());
        return savedProduct;
    }

    public Product getProductById(String id) {
        log.debug("Fetching product with id: {}", id);
        return productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product not found: {}", id);
                    return new ResourceNotFoundException("Product not found: " + id);
                });
    }

    public List<Product> getAllProducts() {
        log.debug("Fetching all products");
        return productRepository.findAll();
    }

    public Product updateProduct(String id, Product updatedProduct) {
        log.debug("Updating product with id: {}", id);

        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product not found: {}", id);
                    return new ResourceNotFoundException("Product not found: " + id);
                });

        if (updatedProduct.getName() != null && !updatedProduct.getName().isEmpty()) {
            existingProduct.setName(updatedProduct.getName());
        }
        if (updatedProduct.getDescription() != null) {
            existingProduct.setDescription(updatedProduct.getDescription());
        }
        if (updatedProduct.getPrice() >= 0) {
            existingProduct.setPrice(updatedProduct.getPrice());
        }
        if (updatedProduct.getSizes() != null && !updatedProduct.getSizes().isEmpty()) {
            existingProduct.setSizes(updatedProduct.getSizes());
        }
        if (updatedProduct.getColors() != null) {
            existingProduct.setColors(updatedProduct.getColors());
        }
        if (updatedProduct.getImageUrl() != null) {
            existingProduct.setImageUrl(updatedProduct.getImageUrl());
        }

        Product savedProduct = productRepository.save(existingProduct);
        log.info("Product updated successfully: {}", savedProduct.getId());
        return savedProduct;
    }

    public void deleteProduct(String id) {
        log.debug("Deleting product with id: {}", id);
        if (!productRepository.existsById(id)) {
            log.warn("Product not found: {}", id);
            throw new ResourceNotFoundException("Product not found: " + id);
        }
        productRepository.deleteById(id);
        log.info("Product deleted successfully: {}", id);
    }
}