package com.emart.dao;

import com.emart.DatabaseConnection;
import com.emart.model.Customer;
import com.emart.model.OrderItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    public int checkout(Customer customer, List<OrderItem> cart) throws SQLException {
        double subtotal = 0.0;
        for (OrderItem item : cart) {
            subtotal += item.quantity * item.price;
        }

        double discountPercent = 0.0;
        if ("Gold".equals(customer.status) || "New".equals(customer.status)) {
            discountPercent = 10.0;
        } else if ("Silver".equals(customer.status)) {
            discountPercent = 5.0;
        }

        double discountAmount = subtotal * (discountPercent / 100.0);
        double shippingFee = 0.0;

        if (subtotal <= 100.0 && !"New".equals(customer.status)) {
            shippingFee = subtotal * 0.10; // 10% shipping handling
        }

        double totalAmount = subtotal - discountAmount + shippingFee;

        String sqlOrder = "INSERT INTO Orders (customer_id, order_date, total_amount, shipping_fee, discount_amount) VALUES (?, SYSDATE, ?, ?, ?)";
        int orderId = -1;

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sqlOrder, new String[]{"ORDER_ID"})) {
                pstmt.setString(1, customer.customerId);
                pstmt.setDouble(2, totalAmount);
                pstmt.setDouble(3, shippingFee);
                pstmt.setDouble(4, discountAmount);
                pstmt.executeUpdate();

                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        orderId = rs.getInt(1);
                    }
                }
            }

            String sqlItems = "INSERT INTO OrderItems (order_id, stock_number, quantity, price) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlItems)) {
                for (OrderItem item : cart) {
                    pstmt.setInt(1, orderId);
                    pstmt.setString(2, item.stockNumber);
                    pstmt.setInt(3, item.quantity);
                    pstmt.setDouble(4, item.price);
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            conn.commit();
            return orderId;
        } catch (SQLException e) {
            throw e;
        }
    }

    public List<OrderItem> getPastOrder(int orderId, String customerId) throws SQLException {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT oi.* FROM OrderItems oi JOIN Orders o ON oi.order_id = o.order_id " +
                     "WHERE o.order_id = ? AND o.customer_id = ? AND o.is_deleted = 0";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            pstmt.setString(2, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    items.add(new OrderItem(rs.getInt("order_id"), rs.getString("stock_number"), rs.getInt("quantity"), rs.getDouble("price")));
                }
            }
        }
        return items;
    }
}
