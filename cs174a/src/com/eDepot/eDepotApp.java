package com.eDepot;

import com.eDepot.dao.*;
import com.eDepot.model.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class eDepotApp {
    private static Scanner scanner = new Scanner(System.in);
    private static InventoryDAO inventoryDAO = new InventoryDAO();
    private static ShippingNoticeDAO shippingNoticeDAO = new ShippingNoticeDAO();
    private static OrderDAO orderDAO = new OrderDAO();

    public static void main(String[] args) {
        System.out.println("==================================");
        System.out.println("       Welcome to eDEPOT          ");
        System.out.println("==================================");

        while (true) {
            try {
                showMenu();
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }
    }

    private static void showMenu() throws SQLException {
        System.out.println("\n--- eDEPOT Warehouse Menu ---");
        System.out.println("1. Receive a Shipping Notice");
        System.out.println("2. Receive a Shipment");
        System.out.println("3. Check Item Quantity");
        System.out.println("4. Fill an Order");
        System.out.println("5. Exit");
        System.out.print("Choose an option: ");
        int choice = Integer.parseInt(scanner.nextLine());

        switch (choice) {
            case 1 -> receiveShippingNotice();
            case 2 -> receiveShipment();
            case 3 -> checkQuantity();
            case 4 -> fillOrder();
            case 5 -> {
                System.out.println("Goodbye!");
                System.exit(0);
            }
            default -> System.out.println("Unknown option.");
        }
    }

    private static void receiveShippingNotice() throws SQLException {
        System.out.print("Shipping company name: ");
        String company = scanner.nextLine();

        int count = readInt("How many distinct items in this notice? ");
        List<ShippingItem> items = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            System.out.println("-- Item " + (i + 1) + " --");
            System.out.print("  Manufacturer: ");
            String mfr = scanner.nextLine();
            System.out.print("  Model number: ");
            String model = scanner.nextLine();
            int qty = readInt("  Quantity: ");

            ShippingItem item = new ShippingItem(mfr, model, qty);
            String existing = inventoryDAO.findStockNumber(mfr, model);
            if (existing == null) {
                System.out.println("  New product. Please provide inventory details.");
                item.isNew = true;
                item.stockNumber = readStockNumber("    Stock number (format XXnnnnn, e.g. AB12345): ");
                item.minStockLevel = readInt("    Minimum stock level: ");
                item.maxStockLevel = readInt("    Maximum stock level: ");
                System.out.print("    Location (e.g. A12): ");
                item.location = scanner.nextLine();
            } else {
                item.stockNumber = existing;
            }
            items.add(item);
        }

        int noticeId = shippingNoticeDAO.receiveShippingNotice(company, items);

        System.out.println("\nShipping notice #" + noticeId + " recorded.");
        for (ShippingItem item : items) {
            if (item.isNew) {
                System.out.println("  New product " + item.manufacturer + " " + item.modelNumber +
                    " registered with stock number " + item.stockNumber);
            } else {
                System.out.println("  Matched " + item.manufacturer + " " + item.modelNumber +
                    " to stock number " + item.stockNumber);
            }
        }
        System.out.println("Reference notice #" + noticeId + " when the shipment arrives.");
    }

    private static void receiveShipment() throws SQLException {
        int noticeId = readInt("Shipping notice id being received: ");
        List<ShippingItem> applied = shippingNoticeDAO.receiveShipment(noticeId);

        if (applied.isEmpty()) {
            System.out.println("No notice found with id " + noticeId + ".");
            return;
        }

        System.out.println("\nShipment for notice #" + noticeId + " received:");
        for (ShippingItem item : applied) {
            System.out.println("  +" + item.quantity + " of " + item.manufacturer + " " + item.modelNumber);
        }
    }

    private static void checkQuantity() throws SQLException {
        System.out.print("Stock number: ");
        String stock = scanner.nextLine();
        int[] levels = inventoryDAO.getByStockNumber(stock);
        if (levels == null) {
            System.out.println("No such stock number.");
        } else {
            System.out.println("Quantity: " + levels[0] + " | Replenishment: " + levels[1]);
        }
    }

    private static void fillOrder() throws SQLException {
        int orderNumber = readInt("Order number being filled: ");

        FillOrderResult result = orderDAO.fillOrder(orderNumber);

        if (result.fulfilled.isEmpty() && result.skipped.isEmpty()) {
            System.out.println("Order #" + orderNumber + " was not found or has no items.");
            return;
        }

        System.out.println("\nOrder #" + orderNumber + " processed.");
        for (String f : result.fulfilled) {
            System.out.println("  Filled: " + f);
        }
        for (String s : result.skipped) {
            System.out.println("  Skipped: " + s);
        }
        for (String r : result.replenishmentOrders) {
            System.out.println("  Replenishment order created: " + r);
        }
    }

    // Prompt until the user enters an unused stock number in the XXnnnnn format: two
    private static String readStockNumber(String prompt) throws SQLException {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim().toUpperCase();
            if (!value.matches("[A-Z]{2}[0-9]{5}")) {
                System.out.println("  Invalid format. Use two uppercase letters + five digits (e.g. AB12345).");
                continue;
            }
            if (inventoryDAO.getByStockNumber(value) != null) {
                System.out.println("  Stock number " + value + " is already in use. Please enter a different one.");
                continue;
            }
            return value;
        }
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  Please enter a whole number.");
            }
        }
    }
}
