package com.emart.dao;

import com.emart.DatabaseConnection;
import java.sql.*;

public class ManagerDAO {

    public boolean loginManager(String managerId, String password) throws SQLException {
        String sql = "SELECT * FROM Managers WHERE manager_id = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, managerId);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void registerManager(String managerId, String password) throws SQLException {
        String sql = "INSERT INTO Managers (manager_id, password) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, managerId);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
        }
    }

    public void printMonthlySummary(int month, int year) throws SQLException {
        String sql = "SELECT p.stock_number, p.manufacturer, p.model_number, p.category, " +
                     "SUM(oi.quantity) as total_qty, SUM(oi.quantity * oi.price) as total_sales " +
                     "FROM Orders o " +
                     "JOIN OrderItems oi ON o.order_id = oi.order_id " +
                     "JOIN Products p ON oi.stock_number = p.stock_number " +
                     "WHERE EXTRACT(MONTH FROM o.order_date) = ? AND EXTRACT(YEAR FROM o.order_date) = ? " +
                     "GROUP BY p.stock_number, p.manufacturer, p.model_number, p.category";
            
        String sqlCat = "SELECT p.category, SUM(oi.quantity) as total_qty, SUM(oi.quantity * oi.price) as total_sales " +
                        "FROM Orders o " +
                        "JOIN OrderItems oi ON o.order_id = oi.order_id " +
                        "JOIN Products p ON oi.stock_number = p.stock_number " +
                        "WHERE EXTRACT(MONTH FROM o.order_date) = ? AND EXTRACT(YEAR FROM o.order_date) = ? " +
                        "GROUP BY p.category";
            
        String sqlCust = "SELECT customer_id, total_spent FROM (" +
                         "  SELECT customer_id, SUM(total_amount) as total_spent " +
                         "  FROM Orders " +
                         "  WHERE EXTRACT(MONTH FROM order_date) = ? AND EXTRACT(YEAR FROM order_date) = ? " +
                         "  GROUP BY customer_id " +
                         "  ORDER BY total_spent DESC" +
                         ") WHERE ROWNUM = 1";
            
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("\n--- Monthly Summary: " + month + "/" + year + " ---");
            
            System.out.println("\n--- Sales per Product ---");
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, month); pstmt.setInt(2, year);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        System.out.printf("Product [%s] %s %s (%s) - Sold: %d, Total: $%.2f%n", 
                            rs.getString("stock_number"), rs.getString("manufacturer"), rs.getString("model_number"),
                            rs.getString("category"), rs.getInt("total_qty"), rs.getDouble("total_sales"));
                    }
                }
            }
            
            System.out.println("\n--- Sales per Category ---");
            try (PreparedStatement pstmt = conn.prepareStatement(sqlCat)) {
                pstmt.setInt(1, month); pstmt.setInt(2, year);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        System.out.printf("Category [%s] - Sold: %d, Total: $%.2f%n", 
                            rs.getString("category"), rs.getInt("total_qty"), rs.getDouble("total_sales"));
                    }
                }
            }
            
            System.out.println("\n--- Top Customer ---");
            try (PreparedStatement pstmt = conn.prepareStatement(sqlCust)) {
                pstmt.setInt(1, month); pstmt.setInt(2, year);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        System.out.printf("Customer ID: %s, Total Spent: $%.2f%n", rs.getString("customer_id"), rs.getDouble("total_spent"));
                    } else {
                        System.out.println("No sales this month.");
                    }
                }
            }
        }
    }

    public void autoAdjustCustomerStatuses() throws SQLException {
        String sql = "SELECT customer_id, SUM(total_amount) as total_sum " +
                     "FROM (" +
                     "  SELECT customer_id, total_amount, " +
                     "         ROW_NUMBER() OVER(PARTITION BY customer_id ORDER BY order_date DESC) as rnk " +
                     "  FROM Orders " +
                     "  WHERE is_deleted = 0" +
                     ") " +
                     "WHERE rnk <= 3 " +
                     "GROUP BY customer_id";
            
        String updateSql = "UPDATE Customers SET status = ? WHERE customer_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery();
             PreparedStatement updatePstmt = conn.prepareStatement(updateSql)) {
            
            int updatedCount = 0;
            while (rs.next()) {
                String cid = rs.getString("customer_id");
                double total = rs.getDouble("total_sum");
                String newStatus = "New";
                if (total > 500) newStatus = "Gold";
                else if (total > 100) newStatus = "Silver";
                else if (total > 0) newStatus = "Green";
                
                updatePstmt.setString(1, newStatus);
                updatePstmt.setString(2, cid);
                updatePstmt.addBatch();
                updatedCount++;
            }
            
            if (updatedCount > 0) {
                updatePstmt.executeBatch();
            }
            System.out.println("Automated status adjustment complete for " + updatedCount + " active customers.");
        }
    }

    public void manualAdjustCustomerStatus(String customerId, String newStatus) throws SQLException {
        String sql = "UPDATE Customers SET status = ? WHERE customer_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newStatus);
            pstmt.setString(2, customerId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Customer " + customerId + " status updated to " + newStatus);
            } else {
                System.out.println("Customer not found.");
            }
        }
    }

    public void sendManufacturerOrder(String manufacturer, String stockNumber, int quantity) {
        System.out.println("\n==============================================");
        System.out.println("              MANUFACTURER ORDER              ");
        System.out.println("==============================================");
        System.out.println("TO: " + manufacturer);
        System.out.println("SHIP TO: eDEPOT WAREHOUSE DIRECTLY");
        System.out.println("ITEM STOCK NO: " + stockNumber);
        System.out.println("QUANTITY ORDERED: " + quantity);
        System.out.println("==============================================");
        System.out.println("Order successfully sent.");
    }

    public void updateProductPrice(String stockNumber, double newPrice) throws SQLException {
        String sql = "UPDATE Products SET price = ? WHERE stock_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, newPrice);
            pstmt.setString(2, stockNumber);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Price updated successfully.");
            } else {
                System.out.println("Product not found.");
            }
        }
    }

    public void deleteOldTransactions() throws SQLException {
        String sql = "UPDATE Orders SET is_deleted = 1 " +
                     "WHERE order_id IN (" +
                     "  SELECT order_id FROM (" +
                     "    SELECT order_id, ROW_NUMBER() OVER(PARTITION BY customer_id ORDER BY order_date DESC) as rnk " +
                     "    FROM Orders " +
                     "    WHERE is_deleted = 0" +
                     "  ) " +
                     "  WHERE rnk > 3" +
                     ")";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int rows = pstmt.executeUpdate();
            System.out.println("Purged " + rows + " old sales transactions (Soft Delete applied).");
        }
    }
}
