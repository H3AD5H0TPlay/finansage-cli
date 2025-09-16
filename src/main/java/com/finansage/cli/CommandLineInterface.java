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
import java.util.Objects;
import java.util.Scanner;

/**
 * Handles all console input and output for the FinanSage application.
 */
public class CommandLineInterface {

    private final TransactionService transactionService;
    private final Scanner scanner;

    public CommandLineInterface(TransactionService transactionService) {
        this.transactionService = Objects.requireNonNull(transactionService, "TransactionService cannot be null.");
        this.scanner = new Scanner(System.in);
    }

    /**
     * Starts the main application loop, displaying the menu and handling user input.
     */
    public void start() {
        boolean running = true;
        while (running) {
            printMenu();
            int choice = getUserChoice();
            switch (choice) {
                case 1:
                    handleAddTransaction();
                    break;
                case 2:
                    handleListTransactions();
                    break;
                case 3:
                    handleDeleteTransaction();
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
        System.out.println("\n===== FinanSage Menu =====");
        System.out.println("1. Add Transaction");
        System.out.println("2. List All Transactions");
        System.out.println("3. Delete Transaction");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
    }

    private int getUserChoice() {
        try {
            int choice = scanner.nextInt();
            scanner.nextLine();
            return choice;
        } catch (InputMismatchException e) {
            scanner.nextLine();
            return -1;
        }
    }

    private void handleListTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }
        System.out.println("\n--- All Transactions ---");
        System.out.printf("%-38s %-12s %-10s %-15s %s%n", "ID", "Date", "Type", "Amount", "Description");
        System.out.println("-".repeat(100));
        for (Transaction t : transactions) {
            System.out.printf("%-38s %-12s %-10s %-15.2f %s (%s)%n",
                    t.getId(),
                    t.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
                    t.getType(),
                    t.getAmount(),
                    t.getDescription(),
                    t.getCategory());
        }
        System.out.println("-".repeat(100));
    }

    private void handleAddTransaction() {
        try {
            System.out.print("Enter date (YYYY-MM-DD): ");
            LocalDate date = LocalDate.parse(scanner.nextLine(), DateTimeFormatter.ISO_LOCAL_DATE);

            System.out.print("Enter description: ");
            String description = scanner.nextLine();

            System.out.print("Enter amount: ");
            BigDecimal amount = scanner.nextBigDecimal();
            scanner.nextLine();

            System.out.print("Enter type (INCOME/EXPENSE): ");
            TransactionType type = TransactionType.valueOf(scanner.nextLine().toUpperCase());

            System.out.print("Enter category: ");
            String category = scanner.nextLine();

            transactionService.addTransaction(date, description, amount, type, category);
            System.out.println("Transaction added successfully.");

        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Please use YYYY-MM-DD.");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid input. Please check transaction type or other values.");
        }
    }

    private void handleDeleteTransaction() {
        System.out.println("\n--- Delete a Transaction ---");
        handleListTransactions();
        List<Transaction> transactions = transactionService.getAllTransactions();
        if (transactions.isEmpty()) {
            return;
        }

        System.out.print("Enter the ID of the transaction to delete: ");
        String idToDelete = scanner.nextLine().trim();

        boolean success = transactionService.deleteTransaction(idToDelete);

        if (success) {
            System.out.println("Transaction deleted successfully.");
        } else {
            System.out.println("Transaction with that ID was not found.");
        }
    }
}

