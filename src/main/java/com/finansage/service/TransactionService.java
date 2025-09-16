package com.finansage.service;

import com.finansage.model.FinancialSummary;
import com.finansage.model.Transaction;
import com.finansage.model.TransactionType;
import com.finansage.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TransactionService {
    private final TransactionRepository repository;
    private List<Transaction> transactions;

    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
        this.transactions = new ArrayList<>(repository.loadTransactions());
    }

    public List<Transaction> getAllTransactions() {
        return new ArrayList<>(transactions); // Return a copy for immutability
    }

    public void addTransaction(LocalDate date, String description, BigDecimal amount, TransactionType type, String category) {
        Transaction newTransaction = new Transaction(date, description, amount, type, category);
        this.transactions.add(newTransaction);
        repository.saveTransactions(this.transactions);
    }

    public boolean deleteTransaction(String id) {
        boolean removed = this.transactions.removeIf(tx -> tx.getId().equals(id));
        if (removed) {
            repository.saveTransactions(this.transactions);
        }
        return removed;
    }

    public FinancialSummary getFinancialSummary() {
        // Calculate total income
        BigDecimal totalIncome = transactions.stream() // 1. Get a stream of transactions
                .filter(tx -> tx.getType() == TransactionType.INCOME) // 2. Keep only INCOME transactions
                .map(Transaction::getAmount) // 3. Get the amount from each one
                .reduce(BigDecimal.ZERO, BigDecimal::add); // 4. Sum them all up, starting from zero

        // Calculate total expenses using the same stream pipeline
        BigDecimal totalExpenses = transactions.stream()
                .filter(tx -> tx.getType() == TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netBalance = totalIncome.subtract(totalExpenses);

        return new FinancialSummary(totalIncome, totalExpenses, netBalance);
    }
}

