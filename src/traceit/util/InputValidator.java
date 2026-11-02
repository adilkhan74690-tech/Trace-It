package traceit.util;

import traceit.model.ItemCategory;

import java.time.LocalDateTime;
import java.util.Scanner;

public class InputValidator {

    public static String readNonEmptyString(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            if (input != null && !input.trim().isEmpty()) {
                return input.trim();
            }
            System.out.println("[Error] Input cannot be empty. Please enter a valid value.");
        }
    }

    public static String readOptionalString(Scanner scanner, String prompt, String defaultValue) {
        System.out.print(prompt + (defaultValue.isEmpty() ? "" : " [" + defaultValue + "]") + ": ");
        String input = scanner.nextLine();
        if (input == null || input.trim().isEmpty()) {
            return defaultValue;
        }
        return input.trim();
    }

    public static int readInt(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            try {
                int val = Integer.parseInt(input.trim());
                if (val >= min && val <= max) {
                    return val;
                }
                System.out.println("[Error] Please enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("[Error] Invalid integer format. Please try again.");
            }
        }
    }

    public static double readDouble(Scanner scanner, String prompt, double min) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            try {
                double val = Double.parseDouble(input.trim());
                if (val >= min) {
                    return val;
                }
                System.out.println("[Error] Value must be at least " + min + ".");
            } catch (NumberFormatException e) {
                System.out.println("[Error] Invalid decimal format. Please try again.");
            }
        }
    }

    public static ItemCategory readCategory(Scanner scanner, String prompt) {
        System.out.println(prompt);
        ItemCategory[] categories = ItemCategory.values();
        for (int i = 0; i < categories.length; i++) {
            System.out.printf("  %2d. %s\n", (i + 1), categories[i].getDisplayName());
        }
        int choice = readInt(scanner, "Select category (1-" + categories.length + "): ", 1, categories.length);
        return categories[choice - 1];
    }

    public static LocalDateTime readDateTime(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt + " (YYYY-MM-DD HH:MM or press Enter for current time): ");
            String input = scanner.nextLine();
            if (input == null || input.trim().isEmpty()) {
                return LocalDateTime.now();
            }
            LocalDateTime dt = DateTimeUtils.parse(input);
            if (dt != null) {
                return dt;
            }
            System.out.println("[Error] Invalid date/time format. Example: 2026-09-30 14:30");
        }
    }
}
