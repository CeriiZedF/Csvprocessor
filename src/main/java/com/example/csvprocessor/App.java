package com.example.csvprocessor;

import com.example.csvprocessor.model.Product;
import com.example.csvprocessor.service.CSVProcessor;
import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class App {

    public static void main(String[] args) {
        CSVProcessor processor = new CSVProcessor();
        try {
            List<Product> productsFromFirstFile = processor.parseCSV("order_1.csv");
            List<Product> productsFromSecondFile = processor.parseCSV("order_2.csv");

            List<Product> allProducts = new ArrayList<>();
            allProducts.addAll(productsFromFirstFile);
            allProducts.addAll(productsFromSecondFile);

            Map<String, List<Product>> productsByStore = allProducts.stream()
                    .collect(Collectors.groupingBy(Product::getStoreName));

            for (Map.Entry<String, List<Product>> entry : productsByStore.entrySet()) {
                String storeName = entry.getKey();
                List<Product> products = entry.getValue();

            }

            processor.writeCSV("results", productsByStore);

        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }

}
