package com.tracksphere.util;

import java.util.Scanner;

/**
 * InputValidator.java
 *
 * Provides reusable validation and safe console input reading methods.
 * Prevents the application from crashing due to improper user inputs.
 * Demonstrates:
 *  1. String manipulation, trimming, regex matching (Lecture 6-9).
 *  2. Exception handling with try-catch for InputMismatchException and NumberFormatException (Lecture 10-15).
 *  3. Static utility methods.
 *
 * Evaluation: Programming in Java (25CSE0202).
 */
public class InputValidator {

    /**
     * Private constructor to prevent direct instantiation of utility class.
     */
    private InputValidator() {
    }

    /**
     * Validates passenger name (must be alphabetic with spaces, 2 to 50 characters).
     *
     * @param name Name string to test
     * @return true if valid, false otherwise
     */
    public static boolean isValidName(String name) {
        if (name == null) {
            return false;
        }
        String trimmed = name.trim();
        if (trimmed.length() < 2 || trimmed.length() > 50) {
            return false;
        }
        // Match only letters and spaces
        return trimmed.matches("^[a-zA-Z\\s]+$");
    }

    /**
     * Validates passenger age (must be between 1 and 120).
     *
     * @param age Age integer
     * @return true if between 1 and 120
     */
    public static boolean isValidAge(int age) {
        return age >= 1 && age <= 120;
    }

    /**
     * Validates Indian 10-digit mobile number (starts with 6, 7, 8, or 9).
     *
     * @param mobile Mobile number string
     * @return true if valid 10-digit mobile number
     */
    public static boolean isValidMobile(String mobile) {
        if (mobile == null) {
            return false;
        }
        String trimmed = mobile.trim();
        return trimmed.matches("^[6-9]\\d{9}$");
    }

    /**
     * Validates a 10-digit PNR number.
     *
     * @param pnr PNR string
     * @return true if exactly 10 digits
     */
    public static boolean isValidPNR(String pnr) {
        if (pnr == null) {
            return false;
        }
        return pnr.trim().matches("^\\d{10}$");
    }

    /**
     * Validates berth preference.
     *
     * @param berth Berth type string
     * @return true if valid berth option
     */
    public static boolean isValidBerth(String berth) {
        if (berth == null) {
            return false;
        }
        String b = berth.trim().toUpperCase();
        return b.equals("LOWER") || b.equals("MIDDLE") || b.equals("UPPER") ||
               b.equals("SIDE_LOWER") || b.equals("SIDE_UPPER") || b.equals("NO_PREFERENCE");
    }

    /**
     * Reads a valid integer from the scanner within a specified range [min, max].
     * Catches NumberFormatException / InputMismatchException so the program never crashes.
     *
     * @param scanner Scanner instance
     * @param prompt  User instruction message
     * @param min     Minimum acceptable value
     * @param max     Maximum acceptable value
     * @return Validated integer
     */
    public static int readValidInteger(Scanner scanner, String prompt, int min, int max) {
        int value = -1;
        boolean valid = false;

        while (!valid) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    valid = true;
                } else {
                    System.out.printf("   [Invalid Input] Please enter a number between %d and %d.\n", min, max);
                }
            } catch (NumberFormatException e) {
                System.out.println("   [Invalid Format] Please enter a valid whole number (no letters or decimals).");
            }
        }
        return value;
    }

    /**
     * Reads a non-empty string from the scanner.
     *
     * @param scanner Scanner instance
     * @param prompt  User instruction message
     * @return Trimmed non-empty string
     */
    public static String readValidString(Scanner scanner, String prompt) {
        String result = "";
        while (result.isEmpty()) {
            System.out.print(prompt);
            result = scanner.nextLine().trim();
            if (result.isEmpty()) {
                System.out.println("   [Required] Input cannot be blank. Please try again.");
            }
        }
        return result;
    }

    /**
     * Reads and validates a 10-digit mobile number from the console.
     */
    public static String readValidMobile(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String mobile = scanner.nextLine().trim();
            if (isValidMobile(mobile)) {
                return mobile;
            }
            System.out.println("   [Invalid Mobile] Please enter a valid 10-digit Indian mobile number (starts with 6, 7, 8, or 9).");
        }
    }

    /**
     * Reads and validates a passenger name.
     */
    public static String readValidPassengerName(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String name = scanner.nextLine().trim();
            if (isValidName(name)) {
                return name;
            }
            System.out.println("   [Invalid Name] Name must contain only alphabets and spaces (2-50 characters).");
        }
    }
}
