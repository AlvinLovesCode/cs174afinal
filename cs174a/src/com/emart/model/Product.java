package com.emart.model;

public class Product {
    public String stockNumber;
    public String manufacturer;
    public String modelNumber;
    public String category;
    public String description;
    public int warrantyMonths;
    public double price;

    public Product(String stockNumber, String manufacturer, String modelNumber, String category, String description, int warrantyMonths, double price) {
        this.stockNumber = stockNumber;
        this.manufacturer = manufacturer;
        this.modelNumber = modelNumber;
        this.category = category;
        this.description = description;
        this.warrantyMonths = warrantyMonths;
        this.price = price;
    }

    @Override
    public String toString() {
        return stockNumber + " - " + manufacturer + " " + modelNumber + " (" + category + ") - $" + price;
    }
}
