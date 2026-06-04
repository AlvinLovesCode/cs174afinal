package com.eDepot.dao;

import com.eDepot.DatabaseConnection;
import java.sql.*;

public class InventoryDAO {

    // Look up an existing stock number for a (manufacturer, model) pair. Null if none.
    public String findStockNumber(String manufacturer, String modelNumber) throws SQLException {
        String sql = "SELECT stock_number FROM Inventory WHERE manufacturer = ? AND model_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, manufacturer);
            pstmt.setString(2, modelNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        }
    }

    // Max stock level for an existing stock number. -1 if none.
    public int getMaxStockLevel(String stockNumber) throws SQLException {
        String sql = "SELECT max_stock_level FROM Inventory WHERE stock_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, stockNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    // Check item quantity by stock number, return {quantity, replenishment} array 
    public int[] getByStockNumber(String stockNumber) throws SQLException {
        String sql = "SELECT quantity, replenishment FROM Inventory WHERE stock_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, stockNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new int[] { rs.getInt("quantity"), rs.getInt("replenishment") };
                }
            }
        }
        return null;
    }
}
