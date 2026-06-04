package com.eDepot.model;

public class OrderItem {
    public String stockNumber;
    public int quantity;

    public OrderItem(String stockNumber, int quantity) {
        this.stockNumber = stockNumber;
        this.quantity = quantity;
    }
}
