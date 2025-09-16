package com.finansage.service;

import com.finansage.model.Transaction;
import com.finansage.model.TransactionType;
import com.finansage.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class TransactionService {

    private final TransactionRepository repository;
    private final List<Transaction> transactions;

    public TransactionService(TransactionRepository repository) {
        this.repository = Objects.requireNonNull(repository, "Repository cannot be null.");
        this.transactions = new CopyOnWriteArrayList<>(this.repository.loadTransactions());
    }

    public List<Transaction> getAllTransactions() {
        return List.copyOf(this.transactions);
    }

    public void addTransaction(LocalDate date, String description, BigDecimal amount, TransactionType type, String category) {
        Transaction newTransaction = new Transaction(null, date, description, amount, type, category);
        this.transactions.add(newTransaction);
        this.repository.saveTransactions(this.transactions);
    }

    public boolean deleteTransaction(String id) {
        boolean removed = this.transactions.removeIf(transaction -> transaction.getId().equals(id));
        if (removed) {
            this.repository.saveTransactions(this.transactions);
        }
        return removed;
    }
}

