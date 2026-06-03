package com.emart;

import com.emart.dao.*;
import com.emart.model.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class eMartApp {
    private static Scanner scanner = new Scanner(System.in);
    private static CustomerDAO customerDAO = new CustomerDAO();
    private static ProductDAO productDAO = new ProductDAO();
    private static OrderDAO orderDAO = new OrderDAO();
    private static ManagerDAO managerDAO = new ManagerDAO();
    
    private static Customer loggedInCustomer = null;
    private static boolean isManagerLoggedIn = false;
    private static List<OrderItem> cart = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("==================================");
        System.out.println("   Welcome to eMart Online Store  ");
        System.out.println("==================================");

        while (true) {
            try {
                if (isManagerLoggedIn) {
                    showManagerMenu();
                } else if (loggedInCustomer != null) {
                    showCustomerMenu();
                } else {
                    showLoginMenu();
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                scanner.nextLine(); // Clear scanner buffer on error
            }
        }
    }

    private static void showLoginMenu() throws SQLException {
        System.out.println("\n1. Login as Customer");
        System.out.println("2. Register as Customer");
        System.out.println("3. Login as Manager");
        System.out.println("4. Register as Manager");
        System.out.println("5. Exit");
        System.out.print("Choose an option: ");
        int choice = Integer.parseInt(scanner.nextLine());
        
        if (choice == 1) {
            System.out.print("Enter Customer ID: ");
            String id = scanner.nextLine();
            System.out.print("Enter Password: ");
            String pw = scanner.nextLine();
            loggedInCustomer = customerDAO.login(id, pw);
            if (loggedInCustomer != null) {
                System.out.println("Login successful! Welcome " + loggedInCustomer.name);
            } else {
                System.out.println("Invalid credentials.");
            }
        } else if (choice == 2) {
            System.out.print("Enter Customer ID: ");
            String id = scanner.nextLine();
            System.out.print("Enter Password: ");
            String pw = scanner.nextLine();
            System.out.print("Enter Name: ");
            String name = scanner.nextLine();
            System.out.print("Enter Email: ");
            String email = scanner.nextLine();
            System.out.print("Enter Address: ");
            String address = scanner.nextLine();
            
            customerDAO.register(new Customer(id, pw, name, email, address, "New"));
            System.out.println("Registration successful! Please login.");
        } else if (choice == 3) {
            System.out.print("Enter Manager ID: ");
            String id = scanner.nextLine();
            System.out.print("Enter Password: ");
            String pw = scanner.nextLine();
            if (managerDAO.loginManager(id, pw)) {
                isManagerLoggedIn = true;
                System.out.println("Manager login successful!");
            } else {
                System.out.println("Invalid manager credentials.");
            }
        } else if (choice == 4) {
            System.out.print("Enter New Manager ID: ");
            String id = scanner.nextLine();
            System.out.print("Enter Password: ");
            String pw = scanner.nextLine();
            managerDAO.registerManager(id, pw);
            System.out.println("Manager registration successful! Please login.");
        } else if (choice == 5) {
            System.out.println("Goodbye!");
            System.exit(0);
        }
    }

    private static void showCustomerMenu() throws SQLException {
        System.out.println("\n--- Customer Menu ---");
        System.out.println("1. Search Products");
        System.out.println("2. View Cart");
        System.out.println("3. Checkout");
        System.out.println("4. Display a Previous Order");
        System.out.println("5. Re-run a Previous Order");
        System.out.println("6. Logout");
        System.out.print("Choose an option: ");
        int choice = Integer.parseInt(scanner.nextLine());

        if (choice == 1) {
            System.out.print("Enter search keyword (or leave blank to list all): ");
            String keyword = scanner.nextLine();
            List<Product> results = productDAO.searchProducts(keyword);
            
            if (results.isEmpty()) {
                System.out.println("No products found.");
                return;
            }

            System.out.println("\n--- Search Results ---");
            for (Product p : results) {
                System.out.println(p);
            }

            System.out.print("\nEnter Stock Number to add to cart (or press enter to go back): ");
            String stockNum = scanner.nextLine();
            if (!stockNum.isEmpty()) {
                Product p = productDAO.getProductByStockNumber(stockNum);
                if (p != null) {
                    System.out.print("Enter Quantity: ");
                    int qty = Integer.parseInt(scanner.nextLine());
                    cart.add(new OrderItem(0, p.stockNumber, qty, p.price));
                    System.out.println("Added to cart!");
                } else {
                    System.out.println("Product not found.");
                }
            }
        } else if (choice == 2) {
            System.out.println("\n--- Cart Contents ---");
            if (cart.isEmpty()) {
                System.out.println("Cart is empty.");
            } else {
                double total = 0;
                for (OrderItem item : cart) {
                    System.out.println("Stock #: " + item.stockNumber + " | Qty: " + item.quantity + " | Price: $" + item.price);
                    total += (item.quantity * item.price);
                }
                System.out.println("Subtotal: $" + total);
            }
        } else if (choice == 3) {
            if (cart.isEmpty()) {
                System.out.println("Cannot checkout. Cart is empty.");
            } else {
                int orderId = orderDAO.checkout(loggedInCustomer, cart);
                System.out.println("Checkout successful! Your Order ID is: " + orderId);
                cart.clear();
            }
        } else if (choice == 4) {
            System.out.print("Enter Order ID to display: ");
            try {
                int orderId = Integer.parseInt(scanner.nextLine());
                List<OrderItem> items = orderDAO.getPastOrder(orderId, loggedInCustomer.customerId);
                if (items.isEmpty()) {
                    System.out.println("Order wasn't found");
                } else {
                    System.out.println("\n--- Order " + orderId + " ---");
                    double total = 0;
                    for (OrderItem item : items) {
                        System.out.println("Stock #: " + item.stockNumber + " | Qty: " + item.quantity + " | Price: $" + item.price);
                        total += (item.quantity * item.price);
                    }
                    System.out.println("Total value: $" + total);
                }
            } catch (NumberFormatException e) {
                System.out.println("Order wasn't found");
            }
        } else if (choice == 5) {
            System.out.print("Enter Order ID to re-run: ");
            try {
                int orderId = Integer.parseInt(scanner.nextLine());
                List<OrderItem> items = orderDAO.getPastOrder(orderId, loggedInCustomer.customerId);
                if (items.isEmpty()) {
                    System.out.println("Order wasn't found");
                } else {
                    for (OrderItem item : items) {
                        Product p = productDAO.getProductByStockNumber(item.stockNumber);
                        if (p != null) {
                            cart.add(new OrderItem(0, p.stockNumber, item.quantity, p.price));
                            System.out.println("Added Stock # " + p.stockNumber + " to cart at current price $" + p.price);
                        }
                    }
                    System.out.println("Previous order items successfully added to cart!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Order wasn't found");
            }
        } else if (choice == 6) {
            loggedInCustomer = null;
            cart.clear();
            System.out.println("Logged out successfully.");
        }
    }

    private static void showManagerMenu() throws SQLException {
        System.out.println("\n--- Manager Menu ---");
        System.out.println("1. Print Monthly Summary");
        System.out.println("2. Auto-Adjust All Customer Statuses");
        System.out.println("3. Manually Override Customer Status");
        System.out.println("4. Send Order to Manufacturer");
        System.out.println("5. Change Product Price");
        System.out.println("6. Purge Old Sales Transactions");
        System.out.println("7. Logout");
        System.out.print("Choose an option: ");
        int choice = Integer.parseInt(scanner.nextLine());

        if (choice == 1) {
            System.out.print("Enter Month (1-12): ");
            int month = Integer.parseInt(scanner.nextLine());
            System.out.print("Enter Year (e.g. 2026): ");
            int year = Integer.parseInt(scanner.nextLine());
            managerDAO.printMonthlySummary(month, year);
        } else if (choice == 2) {
            managerDAO.autoAdjustCustomerStatuses();
        } else if (choice == 3) {
            System.out.print("Enter Customer ID: ");
            String cid = scanner.nextLine();
            System.out.print("Enter New Status (Gold/Silver/Green/New): ");
            String status = scanner.nextLine();
            managerDAO.manualAdjustCustomerStatus(cid, status);
        } else if (choice == 4) {
            System.out.print("Enter Manufacturer Name: ");
            String mfg = scanner.nextLine();
            System.out.print("Enter Stock Number: ");
            String stockNum = scanner.nextLine();
            System.out.print("Enter Quantity to Order: ");
            int qty = Integer.parseInt(scanner.nextLine());
            managerDAO.sendManufacturerOrder(mfg, stockNum, qty);
        } else if (choice == 5) {
            System.out.print("Enter Stock Number: ");
            String stockNum = scanner.nextLine();
            System.out.print("Enter New Price: ");
            double newPrice = Double.parseDouble(scanner.nextLine());
            managerDAO.updateProductPrice(stockNum, newPrice);
        } else if (choice == 6) {
            System.out.println("Warning: This will flag all orders older than the last 3 per customer as deleted.");
            System.out.print("Are you sure? (y/n): ");
            String confirm = scanner.nextLine();
            if (confirm.equalsIgnoreCase("y")) {
                managerDAO.deleteOldTransactions();
            } else {
                System.out.println("Cancelled.");
            }
        } else if (choice == 7) {
            isManagerLoggedIn = false;
            System.out.println("Manager logged out.");
        }
    }
}
