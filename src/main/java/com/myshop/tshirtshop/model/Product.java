package com.myshop.tshirtshop.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    private String id;

    private String name;
    private String description;
    private double price;
    private List<String> sizes;
    private List<String> colors;
    private String imageUrl;

    @CreatedDate
    private LocalDateTime createdAt;
}