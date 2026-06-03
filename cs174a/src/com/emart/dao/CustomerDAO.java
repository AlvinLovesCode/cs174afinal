package com.emart.dao;

import com.emart.DatabaseConnection;
import com.emart.model.Customer;
import java.sql.*;

public class CustomerDAO {
    public Customer login(String customerId, String password) throws SQLException {
        String sql = "SELECT * FROM Customers WHERE customer_id = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, customerId);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Customer(rs.getString("customer_id"), rs.getString("password"), 
                            rs.getString("name"), rs.getString("email"), 
                            rs.getString("address"), rs.getString("status"));
                }
            }
        }
        return null;
    }

    public void register(Customer c) throws SQLException {
        String sql = "INSERT INTO Customers (customer_id, password, name, email, address, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, c.customerId);
            pstmt.setString(2, c.password);
            pstmt.setString(3, c.name);
            pstmt.setString(4, c.email);
            pstmt.setString(5, c.address);
            pstmt.setString(6, "New");
            pstmt.executeUpdate();
        }
    }
}
