package com.example.csvprocessor;

import com.example.csvprocessor.model.Product;
import com.example.csvprocessor.service.CSVProcessor;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CSVProcessorTest {

    private CSVProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new CSVProcessor();
    }

    @Test
    void testMergeProducts_SingleProduct() {
        List<Product> products = List.of(new Product("АТБ", "Гречка", 30.25, 120));
        Map<String, List<Product>> grouped = processor.groupByStoreAndMerge(products);
        List<Product> merged = grouped.get("АТБ");
        assertEquals(1, merged.size());
        assertEquals("Гречка", merged.get(0).getProductName());
        assertEquals(120, merged.get(0).getQuantity());
        assertEquals(30.25, merged.get(0).getPrice(), 0.01);
    }

    @Test
    void testMergeProducts_MultipleProductsSameStore() {
        List<Product> products = List.of(
                new Product("АТБ", "Гречка", 30.25, 120),
                new Product("АТБ", "Гречка", 31.25, 100)
        );
        Map<String, List<Product>> grouped = processor.groupByStoreAndMerge(products);
        List<Product> merged = grouped.get("АТБ");
        assertEquals(1, merged.size());
        assertEquals("Гречка", merged.get(0).getProductName());
        assertEquals(220, merged.get(0).getQuantity());
        assertEquals(30.75, merged.get(0).getPrice(), 0.01);
    }

    @Test
    void testMergeProducts_DifferentStores() {
        List<Product> products = List.of(
                new Product("АТБ", "Гречка", 30.25, 120),
                new Product("Сильпо", "Гречка", 31.25, 100)
        );
        Map<String, List<Product>> grouped = processor.groupByStoreAndMerge(products);
        assertEquals(1, grouped.get("АТБ").size());
        assertEquals(1, grouped.get("Сильпо").size());
    }

    @Test
    void testMergeProducts_EmptyList() {
        List<Product> products = List.of();
        Map<String, List<Product>> grouped = processor.groupByStoreAndMerge(products);
        assertEquals(0, grouped.size());
    }

    @Test
    void testMergeProducts_SingleProductMultipleStores() {
        List<Product> products = List.of(
                new Product("АТБ", "Гречка", 30.25, 120),
                new Product("Сильпо", "Гречка", 31.25, 100)
        );
        Map<String, List<Product>> grouped = processor.groupByStoreAndMerge(products);
        assertEquals(1, grouped.get("АТБ").size());
        assertEquals(1, grouped.get("Сильпо").size());
    }


    @Test
    void testMergeProducts_WithZeroQuantities() {
        List<Product> products = List.of(
                new Product("АТБ", "Гречка", 30.25, 0),
                new Product("АТБ", "Гречка", 31.25, 0)
        );
        Map<String, List<Product>> grouped = processor.groupByStoreAndMerge(products);
        List<Product> merged = grouped.get("АТБ");
        assertEquals(1, merged.size());
        assertEquals("Гречка", merged.get(0).getProductName());
        assertEquals(0, merged.get(0).getQuantity());
        assertEquals(30.75, merged.get(0).getPrice(), 0.01);
    }

    @Test
    void testMergeProducts_IdenticalProducts() {
        List<Product> products = List.of(
                new Product("АТБ", "Гречка", 30.25, 120),
                new Product("АТБ", "Гречка", 30.25, 120)
        );
        Map<String, List<Product>> grouped = processor.groupByStoreAndMerge(products);
        List<Product> merged = grouped.get("АТБ");
        assertEquals(1, merged.size());
        assertEquals("Гречка", merged.get(0).getProductName());
        assertEquals(240, merged.get(0).getQuantity());
        assertEquals(30.25, merged.get(0).getPrice(), 0.01);
    }
}
