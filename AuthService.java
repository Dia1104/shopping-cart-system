package com.demos;

import java.sql.*;
import java.util.Scanner;
import java.util.regex.Pattern;

public class AuthService {
    private static final Pattern STRONG_PASSWORD = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");

    static void center(String text, int width) {
        int pad = (width - text.length()) / 2;
        System.out.println(" ".repeat(Math.max(0, pad)) + text);
    }

    static void header(String title) {
        int width = 64;
        System.out.println("\n" + "═".repeat(width));
        center(title, width);
        System.out.println("═".repeat(width));
    }

    static void separator() {
        System.out.println("─".repeat(64));
    }

    static void successBox(String message) {
        System.out.println("\n" + "┌".repeat(64));
        center("✅ " + message, 64);
        System.out.println("└".repeat(64) + "\n");
    }

    static void errorBox(String message) {
        System.out.println("\n" + "┌".repeat(64));
        center("❌ " + message, 64);
        System.out.println("└".repeat(64) + "\n");
    }

    public static void register(Scanner sc) {
        try {
            Connection con = DBConnection.getConnection();
            header("🔐 CREATE NEW ACCOUNT");

            System.out.print("👤 Enter Username (3+ chars): ");
            String username = sc.nextLine().trim().toLowerCase();

            if (!isValidUsername(username)) {
                errorBox("Username must be 3+ characters (letters/numbers/_ only)");
                return;
            }

            if (userExists(con, username)) {
                errorBox("❌ Username '" + username + "' already taken!");
                return;
            }

            String password = getSecurePassword(sc);

            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO users(username, password) VALUES(?, ?)",
                Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            int userId = keys.next() ? keys.getInt(1) : 0;

            showRegistrationSuccess(username, userId);
            
        } catch (Exception e) {
            errorBox("Registration failed: " + e.getMessage());
        }
    }

    public static int login(Scanner sc) {
        try {
            Connection con = DBConnection.getConnection();
            header("🚪 SECURE LOGIN");

            System.out.print("👤 Username: ");
            String username = sc.nextLine().trim().toLowerCase();

            System.out.print("🔒 Password: ");
            String password = sc.nextLine();

            if (username.isEmpty() || password.isEmpty()) {
                errorBox("Username and Password cannot be empty!");
                return -1;
            }

            int userId = authenticateUser(con, username, password);
            
            if (userId != -1) {
                showLoginSuccess(username, userId);
                return userId;
            } else {
                errorBox("❌ Invalid credentials!");
                return -1;
            }

        } catch (Exception e) {
            errorBox("Login failed: " + e.getMessage());
            return -1;
        }
    }

    private static boolean isValidUsername(String username) {
        return username.length() >= 3 && username.matches("^[a-zA-Z0-9_]+$");
    }

    private static String getSecurePassword(Scanner sc) {
        while (true) {
            System.out.print("🔐 Enter Password (8+ chars, 1 uppercase, 1 number, 1 special): ");
            String password = sc.nextLine().trim();

            if (!STRONG_PASSWORD.matcher(password).matches()) {
                System.out.println("   ⚠️  Invalid! Needs: 8+ chars, Uppercase, Number, Special char\n");
                continue;
            }

            System.out.print("🔐 Confirm Password: ");
            String confirm = sc.nextLine().trim();
            
            if (!password.equals(confirm)) {
                System.out.println("   ⚠️  Passwords don't match!\n");
                continue;
            }
            return password;
        }
    }

    private static boolean userExists(Connection con, String username) throws SQLException {
        PreparedStatement check = con.prepareStatement("SELECT 1 FROM users WHERE username=?");
        check.setString(1, username);
        return check.executeQuery().next();
    }

    private static int authenticateUser(Connection con, String username, String password) throws SQLException {
        PreparedStatement ps = con.prepareStatement(
            "SELECT user_id FROM users WHERE username=? AND password=?"
        );
        ps.setString(1, username);
        ps.setString(2, password);
        ResultSet rs = ps.executeQuery();
        return rs.next() ? rs.getInt("user_id") : -1;
    }

    private static void showRegistrationSuccess(String username, int userId) {
        separator();
        System.out.println("🎉 REGISTRATION COMPLETE!");
        System.out.printf("│ Username: %-20s │ User ID: %d          │\n", username, userId);
        System.out.println("│ Login now to start shopping!                    │");
        separator();
        successBox("Account Ready - Welcome to SmartCart!");
    }

    private static void showLoginSuccess(String username, int userId) {
        separator();
        System.out.println("🚀 ACCESS GRANTED!");
        System.out.printf("│ Welcome %-20s │ User ID: %d          │\n", username, userId);
        separator();
        successBox("Logged In Successfully!");
    }
}