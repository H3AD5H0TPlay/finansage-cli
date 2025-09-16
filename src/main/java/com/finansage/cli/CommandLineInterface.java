package com.finansage.cli;

import com.finansage.model.Transaction;
import com.finansage.model.TransactionType;
import com.finansage.service.TransactionService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class CommandLineInterface {
    private final TransactionService transactionService;
    private final Scanner scanner;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public CommandLineInterface(TransactionService transactionService) {
        this.transactionService = transactionService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Enter your choice: ");
            switch (choice) {
                case 1:
                    addTransaction();
                    break;
                case 2:
                    listTransactions();
                    break;
                case 3:
                    deleteTransaction();
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
        System.out.println("Thank you for using FinanSage. Goodbye!");
    }

    private void printMenu() {
        System.out.println("\n--- FinanSage Menu ---");
        System.out.println("1. Add Transaction");
        System.out.println("2. List all Transactions");
        System.out.println("3. Delete Transaction");
        System.out.println("0. Exit");
        System.out.println("----------------------");
    }

    private void listTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }
        System.out.println("\n--- All Transactions ---");
        System.out.printf("%-38s %-12s %-15s %-10s %-15s %-20s%n", "ID", "Date", "Description", "Amount", "Type", "Category");
        System.out.println("-".repeat(120));
        for (Transaction tx : transactions) {
            System.out.printf("%-38s %-12s %-15s %-10.2f %-15s %-20s%n",
                    tx.getId(),
                    tx.getDate().format(DATE_FORMATTER),
                    tx.getDescription(),
                    tx.getAmount(),
                    tx.getType(),
                    tx.getCategory());
        }
    }

    private void addTransaction() {
        System.out.println("\n--- Add New Transaction ---");
        LocalDate date = readDate("Enter date (YYYY-MM-DD): ");
        String description = readString("Enter description: ");
        BigDecimal amount = readBigDecimal("Enter amount: ");
        TransactionType type = readTransactionType("Enter type (1 for INCOME, 2 for EXPENSE): ");
        String category = readString("Enter category: ");

        transactionService.addTransaction(date, description, amount, type, category);
        System.out.println("Transaction added successfully!");
    }

    private void deleteTransaction() {
        System.out.println("\n--- Delete Transaction ---");
        listTransactions();
        if (transactionService.getAllTransactions().isEmpty()) {
            return;
        }
        String id = readString("Enter the ID of the transaction to delete: ");
        boolean deleted = transactionService.deleteTransaction(id);
        if (deleted) {
            System.out.println("Transaction deleted successfully.");
        } else {
            System.out.println("Error: Transaction with that ID was not found.");
        }
    }

    // --- New Validation Helper Methods ---

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = scanner.nextInt();
                scanner.nextLine();
                return value;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a whole number.");
                scanner.nextLine();
            }
        }
    }

    private BigDecimal readBigDecimal(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                BigDecimal value = new BigDecimal(scanner.nextLine());
                if (value.compareTo(BigDecimal.ZERO) < 0) {
                    System.out.println("Invalid input. Amount cannot be negative.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number (e.g., 50.75).");
            }
        }
    }

    private LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            try {
                return LocalDate.parse(input, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            }
        }
    }

    private String readString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            if (input == null || input.trim().isEmpty()) {
                System.out.println("Invalid input. This field cannot be empty.");
            } else {
                return input.trim();
            }
        }
    }

    private TransactionType readTransactionType(String prompt) {
        while (true) {
            int choice = readInt(prompt);
            switch (choice) {
                case 1:
                    return TransactionType.INCOME;
                case 2:
                    return TransactionType.EXPENSE;
                default:
                    System.out.println("Invalid choice. Please enter 1 or 2.");
            }
        }
    }
}

