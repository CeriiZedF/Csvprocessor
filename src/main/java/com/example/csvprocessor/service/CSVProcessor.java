package com.example.csvprocessor.service;

import com.example.csvprocessor.model.Product;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class CSVProcessor {

    public List<Product> parseCSV(String fileName) throws IOException, CsvException {
        try (CSVReader reader = new CSVReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(fileName)))) {
            return reader.readAll().stream()
                    .skip(1) // Пропускаем заголовок
                    .filter(data -> data.length >= 4) // Фильтруем пустые строки
                    .map(data -> new Product(data[0], data[1], Double.parseDouble(data[2]), Integer.parseInt(data[3])))
                    .collect(Collectors.toList());
        }
    }


    public Map<String, List<Product>> groupByStoreAndMerge(List<Product> products) {
        return products.stream()
                .collect(Collectors.groupingBy(Product::getStoreName,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                this::mergeProducts
                        )));
    }

    private List<Product> mergeProducts(List<Product> products) {
        return products.stream()
                .collect(Collectors.groupingBy(Product::getProductName))
                .entrySet().stream()
                .map(entry -> {
                    String name = entry.getKey();
                    List<Product> sameProducts = entry.getValue();
                    double totalPrice = sameProducts.stream().mapToDouble(Product::getPrice).average().orElse(0);
                    int totalQuantity = sameProducts.stream().mapToInt(Product::getQuantity).sum();
                    return new Product(sameProducts.get(0).getStoreName(), name, totalPrice, totalQuantity);
                })
                .collect(Collectors.toList());
    }

    public void writeCSV(String fileName, Map<String, List<Product>> productsByStore) throws IOException {
        for (Map.Entry<String, List<Product>> entry : productsByStore.entrySet()) {
            String storeName = entry.getKey();
            List<Product> products = entry.getValue();

            String outputFileName = storeName + "_res.csv";
            try (CSVWriter writer = new CSVWriter(new FileWriter(outputFileName))) {
                String[] header = {"НАИМЕНОВАНИЕ", "ЦЕНА", "ШТ"};
                writer.writeNext(header);

                for (Product product : products) {
                    String[] data = {product.getProductName(), String.valueOf(product.getPrice()), String.valueOf(product.getQuantity())};
                    writer.writeNext(data);
                }
            }
        }
    }

}
