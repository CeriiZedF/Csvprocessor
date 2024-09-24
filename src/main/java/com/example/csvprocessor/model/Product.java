package com.example.csvprocessor.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private String storeName;
    private String productName;
    private double price;
    private int quantity;
}
