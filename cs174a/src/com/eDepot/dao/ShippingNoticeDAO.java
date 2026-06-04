package com.eDepot.dao;

import com.eDepot.DatabaseConnection;
import com.eDepot.model.ShippingItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShippingNoticeDAO {

    // Add a new shipping notice and its items
    public int receiveShippingNotice(String shippingCompany, List<ShippingItem> items) throws SQLException {
        String insertNotice = "INSERT INTO ShippingNotice (shipping_company) VALUES (?)";
        String insertInventory =
            "INSERT INTO Inventory (stock_number, manufacturer, model_number, quantity, " +
            "min_stock_level, max_stock_level, location, replenishment) VALUES (?, ?, ?, 0, ?, ?, ?, ?)";
        String addReplenishment =
            "UPDATE Inventory SET replenishment = NVL(replenishment, 0) + ? WHERE stock_number = ?";
        String insertItem =
            "INSERT INTO ShippingNoticeItem (notice_id, manufacturer, model_number, quantity) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int noticeId;
                try (PreparedStatement pstmt = conn.prepareStatement(insertNotice, new String[]{"notice_id"})) {
                    pstmt.setString(1, shippingCompany);
                    pstmt.executeUpdate();
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        rs.next();
                        noticeId = rs.getInt(1);
                    }
                }

                for (ShippingItem item : items) {
                    if (item.isNew) {
                        try (PreparedStatement pstmt = conn.prepareStatement(insertInventory)) {
                            pstmt.setString(1, item.stockNumber);
                            pstmt.setString(2, item.manufacturer);
                            pstmt.setString(3, item.modelNumber);
                            pstmt.setInt(4, item.minStockLevel);
                            pstmt.setInt(5, item.maxStockLevel);
                            pstmt.setString(6, item.location);
                            pstmt.setInt(7, item.quantity);
                            pstmt.executeUpdate();
                        }
                    } else {
                        try (PreparedStatement pstmt = conn.prepareStatement(addReplenishment)) {
                            pstmt.setInt(1, item.quantity);
                            pstmt.setString(2, item.stockNumber);
                            pstmt.executeUpdate();
                        }
                    }

                    try (PreparedStatement pstmt = conn.prepareStatement(insertItem)) {
                        pstmt.setInt(1, noticeId);
                        pstmt.setString(2, item.manufacturer);
                        pstmt.setString(3, item.modelNumber);
                        pstmt.setInt(4, item.quantity);
                        pstmt.executeUpdate();
                    }
                }

                conn.commit();
                return noticeId;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    // Receive physical shipment for a prior notice
    public List<ShippingItem> receiveShipment(int noticeId) throws SQLException {
        String selectItems =
            "SELECT manufacturer, model_number, quantity FROM ShippingNoticeItem WHERE notice_id = ?";
        String updateInventory =
            "UPDATE Inventory SET quantity = quantity + ?, replenishment = NVL(replenishment, 0) - ? " +
            "WHERE manufacturer = ? AND model_number = ?";

        List<ShippingItem> applied = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement pstmt = conn.prepareStatement(selectItems)) {
                    pstmt.setInt(1, noticeId);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        while (rs.next()) {
                            applied.add(new ShippingItem(rs.getString("manufacturer"),
                                rs.getString("model_number"), rs.getInt("quantity")));
                        }
                    }
                }

                if (applied.isEmpty()) {
                    conn.rollback();
                    return applied;
                }

                try (PreparedStatement pstmt = conn.prepareStatement(updateInventory)) {
                    for (ShippingItem item : applied) {
                        pstmt.setInt(1, item.quantity);
                        pstmt.setInt(2, item.quantity);
                        pstmt.setString(3, item.manufacturer);
                        pstmt.setString(4, item.modelNumber);
                        pstmt.addBatch();
                    }
                    pstmt.executeBatch();
                }

                conn.commit();
                return applied;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }
}
