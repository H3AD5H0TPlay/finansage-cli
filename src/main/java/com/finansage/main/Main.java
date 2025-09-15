package com.finansage.main;

import com.finansage.model.Transaction;
import com.finansage.model.TransactionType;
import com.finansage.service.TransactionService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("Welcome to FinanSage - Your Personal Finance Manager!");
        System.out.println("----------------------------------------------------");

        TransactionService transactionService = new TransactionService();

        transactionService.addTransaction(LocalDate.now(), "Monthly Salary", new BigDecimal("3500.00"), TransactionType.INCOME, "Salary");
        transactionService.addTransaction(LocalDate.now().minusDays(2), "Groceries", new BigDecimal("75.50"), TransactionType.EXPENSE, "Food");
        transactionService.addTransaction(LocalDate.now().minusDays(1), "Internet Bill", new BigDecimal("60.00"), TransactionType.EXPENSE, "Utilities");

        System.out.println("\n--- All Transactions ---");

        List<Transaction> allTransactions = transactionService.getAllTransactions();

        if(allTransactions.isEmpty()) {
            System.out.println("No transactions found.");
        } else {
            allTransactions.forEach(System.out::println);
        }

        System.out.println("----------------------------------------------------");
    }
}

