package com.finansage.service;

import com.finansage.model.Transaction;
import com.finansage.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionService {
    private final List<Transaction> transactions = new ArrayList<>();

    public void addTransaction(LocalDate date, String description, BigDecimal amount, TransactionType type, String category) {
        Transaction newTransaction = new Transaction(date, description, amount, type, category);
        this.transactions.add(newTransaction);
        System.out.println("Successfully added transaction: " + newTransaction.getDescription());
    }

    public List<Transaction> getAllTransactions() {
        return new ArrayList<>(this.transactions);
    }
}
