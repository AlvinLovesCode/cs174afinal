package com.eDepot.dao;

import com.eDepot.DatabaseConnection;
import com.eDepot.model.FillOrderResult;
import com.eDepot.model.OrderItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    // Fill an order identified only by its order number and decrement inventory as needed
    // Also send a replenishment order if needed
    public FillOrderResult fillOrder(int orderNumber) throws SQLException {
        FillOrderResult result = new FillOrderResult();

        String selectOrderItems = "SELECT stock_number, quantity FROM OrderItems WHERE order_id = ?";
        String lockRow = "SELECT manufacturer, quantity FROM Inventory WHERE stock_number = ? FOR UPDATE";
        String decrement = "UPDATE Inventory SET quantity = quantity - ? WHERE stock_number = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Pull the order's line items from the eMART order tables.
                List<OrderItem> lines = new ArrayList<>();
                try (PreparedStatement pstmt = conn.prepareStatement(selectOrderItems)) {
                    pstmt.setInt(1, orderNumber);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        while (rs.next()) {
                            lines.add(new OrderItem(rs.getString("stock_number"), rs.getInt("quantity")));
                        }
                    }
                }

                List<String> affectedManufacturers = new ArrayList<>();

                for (OrderItem line : lines) {
                    String manufacturer;
                    int onHand;
                    try (PreparedStatement pstmt = conn.prepareStatement(lockRow)) {
                        pstmt.setString(1, line.stockNumber);
                        try (ResultSet rs = pstmt.executeQuery()) {
                            if (!rs.next()) {
                                result.skipped.add(line.stockNumber + " (no such stock number)");
                                continue;
                            }
                            manufacturer = rs.getString("manufacturer");
                            onHand = rs.getInt("quantity");
                        }
                    }

                    if (onHand < line.quantity) {
                        result.skipped.add(line.stockNumber + " (only " + onHand + " on hand)");
                        continue;
                    }

                    try (PreparedStatement pstmt = conn.prepareStatement(decrement)) {
                        pstmt.setInt(1, line.quantity);
                        pstmt.setString(2, line.stockNumber);
                        pstmt.executeUpdate();
                    }
                    result.fulfilled.add(line.stockNumber + " x" + line.quantity +
                        " (now " + (onHand - line.quantity) + ")");
                    if (!affectedManufacturers.contains(manufacturer)) {
                        affectedManufacturers.add(manufacturer);
                    }
                }

                for (String manufacturer : affectedManufacturers) {
                    if (countBelowMin(conn, manufacturer) >= 3) {
                        int repId = createReplenishmentOrder(conn, manufacturer);
                        result.replenishmentOrders.add("#" + repId + " -> " + manufacturer);
                    }
                }

                conn.commit();
                return result;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    // How many of this manufacturer's items are currently below their min level.
    private int countBelowMin(Connection conn, String manufacturer) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Inventory WHERE manufacturer = ? AND quantity < min_stock_level";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, manufacturer);
            try (ResultSet rs = pstmt.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    // Create a replenishment order for every product of this manufacturer that is
    // below its max level, requesting enough to top up to the max (less in-transit).
    private int createReplenishmentOrder(Connection conn, String manufacturer) throws SQLException {
        int repId;
        String insertHeader = "INSERT INTO ReplenishmentOrder (manufacturer) VALUES (?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertHeader, new String[]{"replenishment_order_id"})) {
            pstmt.setString(1, manufacturer);
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                rs.next();
                repId = rs.getInt(1);
            }
        }

        String insertItems =
            "INSERT INTO ReplenishmentOrderItem (replenishment_order_id, stock_number, quantity_requested) " +
            "SELECT ?, stock_number, (max_stock_level - quantity - NVL(replenishment, 0)) " +
            "FROM Inventory WHERE manufacturer = ? AND (max_stock_level - quantity - NVL(replenishment, 0)) > 0";
        try (PreparedStatement pstmt = conn.prepareStatement(insertItems)) {
            pstmt.setInt(1, repId);
            pstmt.setString(2, manufacturer);
            pstmt.executeUpdate();
        }
        return repId;
    }
}
