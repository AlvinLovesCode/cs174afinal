package com.emart.model;

public class OrderItem {
    public int orderId;
    public String stockNumber;
    public int quantity;
    public double price;

    public OrderItem(int orderId, String stockNumber, int quantity, double price) {
        this.orderId = orderId;
        this.stockNumber = stockNumber;
        this.quantity = quantity;
        this.price = price;
    }
}
