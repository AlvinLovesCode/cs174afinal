package com.eDepot.model;

public class InventoryItem {
    public String stockNumber;
    public String manufacturer;
    public String modelNumber;
    public int quantity;
    public int minStockLevel;
    public int maxStockLevel;
    public String location;
    public int replenishment;

    public InventoryItem(String stockNumber, String manufacturer, String modelNumber, int quantity,
                         int minStockLevel, int maxStockLevel, String location, int replenishment) {
        this.stockNumber = stockNumber;
        this.manufacturer = manufacturer;
        this.modelNumber = modelNumber;
        this.quantity = quantity;
        this.minStockLevel = minStockLevel;
        this.maxStockLevel = maxStockLevel;
        this.location = location;
        this.replenishment = replenishment;
    }

    @Override
    public String toString() {
        return stockNumber + " - " + manufacturer + " " + modelNumber +
               " | Quantity: " + quantity + " | Replenishment: " + replenishment +
               " | Min/Max: " + minStockLevel + "/" + maxStockLevel +
               " | Location: " + location;
    }
}
