package com.emart.model;

public class Customer {
    public String customerId;
    public String password;
    public String name;
    public String email;
    public String address;
    public String status;

    public Customer(String customerId, String password, String name, String email, String address, String status) {
        this.customerId = customerId;
        this.password = password;
        this.name = name;
        this.email = email;
        this.address = address;
        this.status = status;
    }
}
