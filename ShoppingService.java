package com.demos;

import java.sql.*;
import java.util.Scanner;

public class ShoppingService {

    static Scanner sc = new Scanner(System.in);

    static void center(String text) {
        int width = 60;
        int pad = (width - text.length()) / 2;
        System.out.println("\n" + "=".repeat(width));
        System.out.println(" ".repeat(Math.max(0, pad)) + text);
        System.out.println("=".repeat(width) + "\n");
    }

    static void header(String title) {
        System.out.println("\n" + "=".repeat(60));
        center(title);
    }

    static void separator() {
        System.out.println("-".repeat(60));
    }

    static void footer() {
        System.out.println("\n" + "=".repeat(60));
    }

    // ===== MENU =====
    public static void userMenu(int userId) throws Exception {
        Connection con = DBConnection.getConnection();

        while (true) {
            header("SMARTCART DASHBOARD");

            System.out.println("│ 1. View Products          │ 5. Sort Products        │");
            System.out.println("│ 2. Add to Cart            │ 6. Checkout             │");
            System.out.println("│ 3. View Cart              │ 7. Bill History         │");
            System.out.println("│ 4. Search Product         │ 8. Transaction History  │");
            System.out.println("│                           │ 9. Logout               │");
            separator();
            System.out.print("Enter your choice (1-9): ");

            int ch = sc.nextInt();
            sc.nextLine();

            switch (ch) {
                case 1: viewProducts(con); break;
                case 2: addToCart(con, userId); break;
                case 3: viewCart(con, userId); break;
                case 4: searchProduct(con); break;
                case 5: sortProducts(con); break;
                case 6: checkout(con, userId); break;
                case 7: viewBills(con, userId); break;
                case 8: viewTransactions(con, userId); break;
                case 9: 
                    header("GOODBYE!");
                    return;
                default:
                    System.out.println("\n❌ Invalid choice! Try again.\n");
            }

            System.out.print("\nPress Enter to continue...");
            sc.nextLine();
        }
    }

    // ===== PRODUCTS =====
    static void viewProducts(Connection con) throws Exception {
        header("ALL PRODUCTS");

        ResultSet rs = con.createStatement().executeQuery("SELECT * FROM products");

        System.out.printf("│ %-4s │ %-25s │ %-10s │ %-8s │\n", "ID", "PRODUCT NAME", "PRICE", "STOCK");
        separator();

        while (rs.next()) {
            System.out.printf("│ %-4d │ %-25s │ ₹%9.2f │ %7d  │\n",
                    rs.getInt("product_id"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getInt("quantity"));
        }
        footer();
    }

    // ===== ADD CART =====
    static void addToCart(Connection con, int userId) throws Exception {
        header("ADD TO CART");

        System.out.print("Enter Product ID: ");
        int pid = sc.nextInt();

        PreparedStatement ps = con.prepareStatement("SELECT quantity FROM products WHERE product_id=?");
        ps.setInt(1, pid);
        ResultSet rs = ps.executeQuery();

        if (!rs.next()) {
            System.out.println("\n❌ Product ID not found!\n");
            return;
        }

        int stock = rs.getInt(1);

        System.out.print("Enter Quantity (Available: " + stock + "): ");
        int qty = sc.nextInt();

        if (qty <= 0 || qty > stock) {
            System.out.println("\n❌ Invalid quantity!\n");
            return;
        }

        PreparedStatement insert = con.prepareStatement(
            "INSERT INTO cart(user_id,product_id,quantity) VALUES(?,?,?)"
        );
        insert.setInt(1, userId);
        insert.setInt(2, pid);
        insert.setInt(3, qty);
        insert.executeUpdate();

        System.out.println("\n✅ Item added to cart!\n");
    }

    // ===== CART =====
    static void viewCart(Connection con, int userId) throws Exception {
        header("YOUR CART");

        PreparedStatement ps = con.prepareStatement(
            "SELECT p.product_id,p.name,p.price,c.quantity FROM cart c JOIN products p ON c.product_id=p.product_id WHERE c.user_id=?"
        );
        ps.setInt(1, userId);

        ResultSet rs = ps.executeQuery();

        double total = 0;
        boolean empty = true;

        System.out.printf("│ %-4s │ %-25s │ %-6s │ %-8s │ %-10s │\n", "ID", "PRODUCT", "QTY", "PRICE", "SUBTOTAL");
        separator();

        while (rs.next()) {
            empty = false;

            int pid = rs.getInt(1);
            String name = rs.getString(2);
            double price = rs.getDouble(3);
            int qty = rs.getInt(4);

            double sub = price * qty;
            total += sub;

            System.out.printf("│ %-4d │ %-25s │ %5d │ ₹%7.2f │ ₹%9.2f │\n",
                    pid, name, qty, price, sub);
        }

        if (empty) {
            System.out.println("│                    YOUR CART IS EMPTY                    │");
        } else {
            separator();
            System.out.printf("│                     TOTAL: ₹%10.2f                       │\n", total);
        }

        footer();
    }

    // ===== SEARCH =====
    static void searchProduct(Connection con) throws Exception {
        header("PRODUCT SEARCH");

        System.out.print("Enter keyword: ");
        String key = sc.nextLine();

        PreparedStatement ps = con.prepareStatement(
            "SELECT product_id,name,price FROM products WHERE LOWER(name) LIKE ?"
        );
        ps.setString(1, "%" + key.toLowerCase() + "%");

        ResultSet rs = ps.executeQuery();

        System.out.printf("│ %-4s │ %-25s │ %-10s │\n", "ID", "PRODUCT", "PRICE");
        separator();

        boolean found = false;

        while (rs.next()) {
            found = true;
            System.out.printf("│ %-4d │ %-25s │ ₹%9.2f │\n",
                    rs.getInt(1),
                    rs.getString(2),
                    rs.getDouble(3));
        }

        if (!found) {
            System.out.println("│                 NO PRODUCTS FOUND!                      │");
        }

        footer();
    }

    // ===== SORT =====
    static void sortProducts(Connection con) throws Exception {
        header("SORTED PRODUCTS");

        ResultSet rs = con.createStatement().executeQuery(
            "SELECT product_id,name,price FROM products ORDER BY price ASC"
        );

        System.out.printf("│ %-4s │ %-25s │ %-10s │\n", "ID", "PRODUCT", "PRICE");
        separator();

        while (rs.next()) {
            System.out.printf("│ %-4d │ %-25s │ ₹%9.2f │\n",
                    rs.getInt(1),
                    rs.getString(2),
                    rs.getDouble(3));
        }

        footer();
    }

    // ===== CHECKOUT =====
    static void checkout(Connection con, int userId) throws Exception {

        PreparedStatement ps = con.prepareStatement(
            "SELECT p.product_id,p.name,p.price,c.quantity FROM cart c JOIN products p ON c.product_id=p.product_id WHERE c.user_id=?"
        );
        ps.setInt(1, userId);

        ResultSet rs = ps.executeQuery();

        double total = 0;
        boolean empty = true;

        header("FINAL BILL");

        while (rs.next()) {
            empty = false;

            int pid = rs.getInt(1);
            String name = rs.getString(2);
            double price = rs.getDouble(3);
            int qty = rs.getInt(4);

            double sub = price * qty;
            total += sub;

            System.out.println(name + " x" + qty + " = ₹" + sub);

            PreparedStatement update = con.prepareStatement(
                "UPDATE products SET quantity=quantity-? WHERE product_id=?"
            );
            update.setInt(1, qty);
            update.setInt(2, pid);
            update.executeUpdate();
        }

        if (empty) {
            System.out.println("Cart is empty!");
            return;
        }

        System.out.println("\nTOTAL: ₹" + total);

        // BILL
        PreparedStatement bill = con.prepareStatement(
            "INSERT INTO bill_history(user_id,total_amount) VALUES(?,?)"
        );
        bill.setInt(1, userId);
        bill.setDouble(2, total);
        bill.executeUpdate();

        // TRANSACTION (FIXED)
        PreparedStatement txn = con.prepareStatement(
            "INSERT INTO transaction_history(user_id,amount) VALUES(?,?)"
        );
        txn.setInt(1, userId);
        txn.setDouble(2, total);
        txn.executeUpdate();

        // CLEAR CART
        con.createStatement().executeUpdate("DELETE FROM cart WHERE user_id=" + userId);

        footer();
    }

    // ===== BILL HISTORY =====
    static void viewBills(Connection con, int userId) throws Exception {
        header("BILL HISTORY");

        PreparedStatement ps = con.prepareStatement(
            "SELECT * FROM bill_history WHERE user_id=? ORDER BY bill_date DESC"
        );
        ps.setInt(1, userId);

        ResultSet rs = ps.executeQuery();

        boolean empty = true;

        while (rs.next()) {
            empty = false;
            System.out.println("Bill ID: " + rs.getInt("bill_id") +
                    "  ₹" + rs.getDouble("total_amount") +
                    "  " + rs.getTimestamp("bill_date"));
        }

        if (empty) {
            System.out.println("No bill history found.");
        }

        footer();
    }

    // ===== TRANSACTION (FIXED FINAL) =====
    static void viewTransactions(Connection con, int userId) throws Exception {
        header("TRANSACTION HISTORY");

        PreparedStatement ps = con.prepareStatement(
            "SELECT * FROM transaction_history WHERE user_id=? ORDER BY txn_date DESC"
        );
        ps.setInt(1, userId);

        ResultSet rs = ps.executeQuery();

        boolean empty = true;

        while (rs.next()) {
            empty = false;
            System.out.println("₹" + rs.getDouble("amount") +
                    "  " + rs.getTimestamp("txn_date"));
        }

        if (empty) {
            System.out.println("No transactions found.");
        }

        footer();
    }
}