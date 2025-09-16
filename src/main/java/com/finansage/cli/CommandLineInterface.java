package com.finansage.cli;

import com.finansage.model.Transaction;
import com.finansage.model.TransactionType;
import com.finansage.service.TransactionService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class CommandLineInterface {

    private final TransactionService transactionService;
    private final Scanner scanner;

    public CommandLineInterface(TransactionService transactionService) {
        this.transactionService = transactionService;
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        boolean running = true;
        while (running) {
            printMenu();
            int choice = getUserChoice();
            switch (choice) {
                case 1:
                    addTransaction();
                    break;
                case 2:
                    listTransactions();
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        System.out.println("Thank you for using FinanSage. Goodbye!");
    }

    private void printMenu() {
        System.out.println("\n--- FinanSage Menu ---");
        System.out.println("1. Add a new transaction");
        System.out.println("2. List all transactions");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
    }

    private int getUserChoice() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void addTransaction() {
        try {
            System.out.print("Enter date (YYYY-MM-DD): ");
            LocalDate date = LocalDate.parse(scanner.nextLine());

            System.out.print("Enter description: ");
            String description = scanner.nextLine();

            System.out.print("Enter amount: ");
            BigDecimal amount = new BigDecimal(scanner.nextLine());

            System.out.print("Enter type (INCOME/EXPENSE): ");
            TransactionType type = TransactionType.valueOf(scanner.nextLine().toUpperCase());

            System.out.print("Enter category: ");
            String category = scanner.nextLine();

            transactionService.addTransaction(date, description, amount, type, category);
            System.out.println("Transaction added successfully!");

        } catch (DateTimeParseException e) {
            System.out.println("Error: Invalid date format. Please use YYYY-MM-DD.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: Invalid input. Please check your values (e.g., amount, type).");
        }
    }

    private void listTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        System.out.println("\n--- All Transactions ---");
        System.out.printf("%-12s | %-25s | %-10s | %-10s | %-15s\n", "Date", "Description", "Amount", "Type", "Category");
        System.out.println("-".repeat(80));
        for (Transaction t : transactions) {
            System.out.printf("%-12s | %-25s | %-10.2f | %-10s | %-15s\n",
                    t.getDate(),
                    t.getDescription(),
                    t.getAmount(),
                    t.getType(),
                    t.getCategory());
        }
    }
}

