package com.emart.dao;

import com.emart.DatabaseConnection;
import com.emart.model.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    public List<Product> searchProducts(String keyword) throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM Products WHERE LOWER(manufacturer) LIKE ? OR LOWER(model_number) LIKE ? " +
                     "OR LOWER(category) LIKE ? OR LOWER(description) LIKE ? OR LOWER(stock_number) LIKE ?";
        String searchParam = "%" + keyword.toLowerCase() + "%";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 1; i <= 5; i++) {
                pstmt.setString(i, searchParam);
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    products.add(new Product(
                        rs.getString("stock_number"), rs.getString("manufacturer"), 
                        rs.getString("model_number"), rs.getString("category"), 
                        rs.getString("description"), rs.getInt("warranty_months"), 
                        rs.getDouble("price")
                    ));
                }
            }
        }
        return products;
    }

    public Product getProductByStockNumber(String stockNumber) throws SQLException {
        String sql = "SELECT * FROM Products WHERE stock_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, stockNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Product(
                        rs.getString("stock_number"), rs.getString("manufacturer"), 
                        rs.getString("model_number"), rs.getString("category"), 
                        rs.getString("description"), rs.getInt("warranty_months"), 
                        rs.getDouble("price")
                    );
                }
            }
        }
        return null;
    }
}
