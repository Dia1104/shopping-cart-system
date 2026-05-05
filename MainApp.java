package com.demos;

import java.sql.*;
import java.util.Scanner;

public class MainApp {

    static Scanner sc = new Scanner(System.in);

    static void center(String text) {
        int width = 60;
        int pad = (width - text.length()) / 2;
        System.out.println(" ".repeat(Math.max(0, pad)) + text);
    }

    static void header(String title) {
        System.out.println("\n" + "=".repeat(60));
        center(title);
        System.out.println("=".repeat(60));
    }

    static void separator() {
        System.out.println("-".repeat(60));
    }

    public static void main(String[] args) throws Exception {
        Connection con = DBConnection.getConnection();

        while (true) {
            header("🛍️ WELCOME TO SMARTCART PRO");
            
            System.out.println("│ 1. 🔐 Register New Account  │");
            System.out.println("│ 2. 🚪 Login to Account      │");
            System.out.println("│ 3. 👋 Exit Application      │");
            separator();
            System.out.print("Enter your choice (1-3): ");
            
            int ch = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (ch) {
                case 1:
                    register(con);
                    break;
                case 2:
                    int userId = login(con);
                    if (userId != -1) {
                        ShoppingService.userMenu(userId);
                    }
                    break;
                case 3:
                    header("👋 THANK YOU FOR USING SMARTCART!");
                    System.out.println("\nHave a great day! 😊");
                    return;
                default:
                    System.out.println("\n❌ Invalid choice! Please select 1, 2, or 3.\n");
            }
            
            System.out.print("\nPress Enter to continue...");
            sc.nextLine();
        }
    }

    static void register(Connection con) throws Exception {
        header("🔐 NEW USER REGISTRATION");
        
        System.out.print("Enter Username: ");
        String user = sc.nextLine().trim();

        if (user.isEmpty() || user.length() < 3) {
            System.out.println("\n❌ Username must be at least 3 characters!\n");
            return;
        }

        System.out.print("Enter Password: ");
        String pass = sc.nextLine().trim();

        if (pass.isEmpty() || pass.length() < 4) {
            System.out.println("\n❌ Password must be at least 4 characters!\n");
            return;
        }

        try {
            // Check if user already exists
            PreparedStatement checkStmt = con.prepareStatement("SELECT user_id FROM users WHERE username=?");
            checkStmt.setString(1, user);
            ResultSet checkRs = checkStmt.executeQuery();
            
            if (checkRs.next()) {
                System.out.println("\n❌ Username '" + user + "' already exists! Please choose another.\n");
                return;
            }

            // Insert new user
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO users(username,password) VALUES(?,?)",
                Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, user);
            ps.setString(2, pass);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            int userId = rs.next() ? rs.getInt(1) : 0;

            separator();
            System.out.println("✅ Registration Successful!");
            System.out.println("   Username: " + user);
            System.out.println("   User ID: " + userId);
            System.out.println("   You can now login with these credentials!");
            separator();
            
        } catch (SQLException e) {
            System.out.println("\n❌ Registration failed! Please try again.\n");
        }
    }

    static int login(Connection con) throws Exception {
        header("🚪 USER LOGIN");
        
        System.out.print("Username: ");
        String user = sc.nextLine().trim();

        System.out.print("Password: ");
        String pass = sc.nextLine().trim();

        if (user.isEmpty() || pass.isEmpty()) {
            System.out.println("\n❌ Username and Password cannot be empty!\n");
            return -1;
        }

        PreparedStatement ps = con.prepareStatement(
            "SELECT user_id FROM users WHERE username=? AND password=?"
        );
        ps.setString(1, user);
        ps.setString(2, pass);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            int userId = rs.getInt("user_id");
            separator();
            System.out.println("✅ Login Successful! Welcome back!");
            System.out.printf("   User ID: %d\n", userId);
            System.out.printf("   Username: %s\n", user);
            separator();
            return userId;
        }

        System.out.println("\n❌ Invalid username or password!");
        System.out.println("   Please check your credentials and try again.\n");
        return -1;
    }
}