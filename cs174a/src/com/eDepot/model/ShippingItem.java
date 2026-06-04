package com.eDepot.model;

public class ShippingItem {
    public String manufacturer;
    public String modelNumber;
    public int quantity;

    public String stockNumber;
    public boolean isNew;

    // Only used when new product being created
    public int minStockLevel;
    public int maxStockLevel;
    public String location;

    public ShippingItem(String manufacturer, String modelNumber, int quantity) {
        this.manufacturer = manufacturer;
        this.modelNumber = modelNumber;
        this.quantity = quantity;
    }
}
