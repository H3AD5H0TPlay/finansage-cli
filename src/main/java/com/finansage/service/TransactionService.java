package com.finansage.service;

import com.finansage.model.Transaction;
import com.finansage.model.TransactionType;
import com.finansage.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * The business logic layer for handling transactions.
 * It coordinates operations between the UI and the data repository.
 */
public class TransactionService {

    private final TransactionRepository repository;
    private List<Transaction> transactions;

    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
        this.transactions = repository.loadTransactions();
    }

    public void addTransaction(LocalDate date, String description, BigDecimal amount, TransactionType type, String category) {
        String id = UUID.randomUUID().toString(); // Generate a unique ID
        Transaction newTransaction = new Transaction(id, date, description, amount, type, category);
        this.transactions.add(newTransaction);
        this.repository.saveTransactions(this.transactions);
    }

    public List<Transaction> getAllTransactions() {
        return List.copyOf(transactions);
    }
}

