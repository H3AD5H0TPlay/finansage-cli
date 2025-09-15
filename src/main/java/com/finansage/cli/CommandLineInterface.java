package com.finansage.cli;

import com.finansage.model.Transaction;
import com.finansage.model.TransactionType;
import com.finansage.service.TransactionService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
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
        seedData();

        boolean running = true;
        while (running) {
            printMenu();
            System.out.print("Enter your choice: ");
            int choice = getUserChoice();

            switch (choice) {
                case 1:
                    addTransactionUI();
                    break;
                case 2:
                    listTransactionsUI();
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
        System.out.println("----------------------");
    }

    private int getUserChoice() {
        try {
            return scanner.nextInt();
        } catch (InputMismatchException e) {
            scanner.next();
            return -1;
        } finally {
            scanner.nextLine();
        }
    }

    private void addTransactionUI() {
        System.out.println("\n--- Add New Transaction ---");
        try {
            System.out.print("Enter date (YYYY-MM-DD): ");
            LocalDate date = LocalDate.parse(scanner.nextLine());

            System.out.print("Enter description: ");
            String description = scanner.nextLine();

            System.out.print("Enter amount: ");
            BigDecimal amount = new BigDecimal(scanner.nextLine());

            System.out.print("Enter type (1 for INCOME, 2 for EXPENSE): ");
            int typeChoice = Integer.parseInt(scanner.nextLine());
            TransactionType type = (typeChoice == 1) ? TransactionType.INCOME : TransactionType.EXPENSE;

            System.out.print("Enter category: ");
            String category = scanner.nextLine();

            transactionService.addTransaction(date, description, amount, type, category);

        } catch (DateTimeParseException e) {
            System.out.println("Error: Invalid date format. Please use YYYY-MM-DD.");
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid number format for amount or type.");
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    private void listTransactionsUI() {
        System.out.println("\n--- All Transactions ---");
        List<Transaction> transactions = transactionService.getAllTransactions();

        if (transactions.isEmpty()) {
            System.out.println("No transactions to display.");
        } else {
            transactions.forEach(System.out::println);
        }
    }

    private void seedData() {
        // We can add some initial data to make testing easier.
        transactionService.addTransaction(LocalDate.now().minusDays(5), "Initial Balance", new BigDecimal("1000.00"), TransactionType.INCOME, "Initial");
        transactionService.addTransaction(LocalDate.now().minusDays(2), "Groceries", new BigDecimal("75.50"), TransactionType.EXPENSE, "Food");
    }
}
