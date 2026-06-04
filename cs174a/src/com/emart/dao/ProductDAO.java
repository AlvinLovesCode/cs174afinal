package com.emart.dao;

import com.emart.DatabaseConnection;
import com.emart.model.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    public List<Product> searchProducts(String keyword) throws SQLException {
        List<Product> products = new ArrayList<>();
        
        String[] tokens;
        if (keyword == null || keyword.trim().isEmpty()) {
            tokens = new String[]{""};
        } else {
            tokens = keyword.trim().toLowerCase().split("\\s+");
        }
        
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM Products WHERE 1=1");
        for (int i = 0; i < tokens.length; i++) {
            sqlBuilder.append(" AND (LOWER(manufacturer) LIKE ? OR LOWER(model_number) LIKE ? " +
                              "OR LOWER(category) LIKE ? OR LOWER(TO_CHAR(description)) LIKE ? OR LOWER(stock_number) LIKE ?)");
        }
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString())) {
            
            int paramIndex = 1;
            for (String token : tokens) {
                String searchParam = "%" + token + "%";
                for (int i = 0; i < 5; i++) {
                    pstmt.setString(paramIndex++, searchParam);
                }
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

    public List<Product> getCompatibleProducts(String stockNumber) throws SQLException {
        List<Product> products = new ArrayList<>();
        // Use IN clause to avoid DISTINCT which causes ORA-00932 with CLOB columns
        String sql = "SELECT * FROM Products WHERE stock_number IN (" +
                     "  SELECT compatible_stock_number FROM ProductCompatibility WHERE stock_number = ? " +
                     "  UNION " +
                     "  SELECT stock_number FROM ProductCompatibility WHERE compatible_stock_number = ?" +
                     ") AND stock_number != ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, stockNumber);
            pstmt.setString(2, stockNumber);
            pstmt.setString(3, stockNumber);
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

    public String findStockNumber(String manufacturer, String modelNumber) throws SQLException {
        String sql = "SELECT stock_number FROM Products WHERE manufacturer = ? AND model_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, manufacturer);
            pstmt.setString(2, modelNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("stock_number");
                }
            }
        }
        return null;
    }
}
