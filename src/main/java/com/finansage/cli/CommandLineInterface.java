package com.finansage.cli;

import com.finansage.model.FinancialSummary;
import com.finansage.model.Transaction;
import com.finansage.model.TransactionType;
import com.finansage.service.TransactionService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
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
                case 4:
                    showSummary();
                    break;
                case 5: // New option
                    editTransaction();
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
        System.out.println("4. Show Financial Summary");
        System.out.println("5. Edit Transaction"); // New option
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
        LocalDate date = readDate("Enter date (YYYY-MM-DD): ", null);
        String description = readString("Enter description: ", null);
        BigDecimal amount = readBigDecimal("Enter amount: ", null);
        TransactionType type = readTransactionType("Enter type (1 for INCOME, 2 for EXPENSE): ", null);
        String category = readString("Enter category: ", null);

        transactionService.addTransaction(date, description, amount, type, category);
        System.out.println("Transaction added successfully!");
    }

    private void deleteTransaction() {
        System.out.println("\n--- Delete Transaction ---");
        listTransactions();
        if (transactionService.getAllTransactions().isEmpty()) {
            return;
        }
        String id = readString("Enter the ID of the transaction to delete: ", null);
        boolean deleted = transactionService.deleteTransaction(id);
        if (deleted) {
            System.out.println("Transaction deleted successfully.");
        } else {
            System.out.println("Error: Transaction with that ID was not found.");
        }
    }

    private void showSummary() {
        System.out.println("\n--- Financial Summary ---");
        FinancialSummary summary = transactionService.getFinancialSummary();
        System.out.printf("Total Income:  %.2f%n", summary.totalIncome());
        System.out.printf("Total Expenses: %.2f%n", summary.totalExpenses());
        System.out.println("-------------------------");
        System.out.printf("Net Balance:   %.2f%n", summary.netBalance());
        System.out.println("-------------------------");
    }


    /**
     * New method for handling the transaction editing workflow.
     */
    private void editTransaction() {
        System.out.println("\n--- Edit Transaction ---");
        listTransactions();
        if (transactionService.getAllTransactions().isEmpty()) {
            return;
        }
        String id = readString("Enter the ID of the transaction to edit: ", null);
        Optional<Transaction> transactionOpt = transactionService.findTransactionById(id);

        if (transactionOpt.isEmpty()) {
            System.out.println("Error: Transaction with that ID was not found.");
            return;
        }

        Transaction oldTx = transactionOpt.get();
        System.out.println("Editing transaction. Press Enter to keep the current value.");

        LocalDate newDate = readDate("Enter new date (" + oldTx.getDate().format(DATE_FORMATTER) + "): ", oldTx.getDate());
        String newDescription = readString("Enter new description (" + oldTx.getDescription() + "): ", oldTx.getDescription());
        BigDecimal newAmount = readBigDecimal("Enter new amount (" + oldTx.getAmount() + "): ", oldTx.getAmount());
        TransactionType newType = readTransactionType("Enter new type (1=INCOME, 2=EXPENSE) (" + oldTx.getType() + "): ", oldTx.getType());
        String newCategory = readString("Enter new category (" + oldTx.getCategory() + "): ", oldTx.getCategory());

        boolean updated = transactionService.updateTransaction(id, newDate, newDescription, newAmount, newType, newCategory);

        if (updated) {
            System.out.println("Transaction updated successfully!");
        } else {
            // This case should be rare since we already found the transaction
            System.out.println("Error: Failed to update the transaction.");
        }
    }


    // --- Refactored Validation Helper Methods ---

    private int readInt(String prompt) {
        // This one doesn't need a default value for our menu
        while (true) {
            System.out.print(prompt);
            try {
                int value = scanner.nextInt();
                scanner.nextLine(); // Consume the rest of the line
                return value;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a whole number.");
                scanner.nextLine(); // Clear the invalid input
            }
        }
    }

    private BigDecimal readBigDecimal(String prompt, BigDecimal defaultValue) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            if (input.isEmpty() && defaultValue != null) {
                return defaultValue;
            }
            try {
                BigDecimal value = new BigDecimal(input);
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

    private LocalDate readDate(String prompt, LocalDate defaultValue) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            if (input.isEmpty() && defaultValue != null) {
                return defaultValue;
            }
            try {
                return LocalDate.parse(input, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            }
        }
    }

    private String readString(String prompt, String defaultValue) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            if (input.isEmpty() && defaultValue != null) {
                return defaultValue;
            }
            if (input.trim().isEmpty()) {
                System.out.println("Invalid input. This field cannot be empty.");
            } else {
                return input.trim();
            }
        }
    }

    private TransactionType readTransactionType(String prompt, TransactionType defaultValue) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            if (input.isEmpty() && defaultValue != null) {
                return defaultValue;
            }
            try {
                int choice = Integer.parseInt(input);
                switch (choice) {
                    case 1:
                        return TransactionType.INCOME;
                    case 2:
                        return TransactionType.EXPENSE;
                    default:
                        System.out.println("Invalid choice. Please enter 1 or 2.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter 1 or 2.");
            }
        }
    }
}

