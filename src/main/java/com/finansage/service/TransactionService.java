package com.finansage.service;

import com.finansage.model.FinancialSummary;
import com.finansage.model.Transaction;
import com.finansage.model.TransactionType;
import com.finansage.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        BigDecimal totalIncome = transactions.stream()
                .filter(tx -> tx.getType() == TransactionType.INCOME)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpenses = transactions.stream()
                .filter(tx -> tx.getType() == TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netBalance = totalIncome.subtract(totalExpenses);

        return new FinancialSummary(totalIncome, totalExpenses, netBalance);
    }

    /**
     * Finds a single transaction by its unique ID.
     * @param id The ID of the transaction to find.
     * @return An Optional containing the transaction if found, otherwise an empty Optional.
     */
    public Optional<Transaction> findTransactionById(String id) {
        return transactions.stream()
                .filter(tx -> tx.getId().equals(id))
                .findFirst();
    }

    /**
     * Updates an existing transaction by replacing it with a new one.
     * @param id The ID of the transaction to update.
     * @param newDate The new date.
     * @param newDescription The new description.
     * @param newAmount The new amount.
     * @param newType The new type.
     * @param newCategory The new category.
     * @return true if the transaction was found and updated, false otherwise.
     */
    public boolean updateTransaction(String id, LocalDate newDate, String newDescription, BigDecimal newAmount, TransactionType newType, String newCategory) {
        Optional<Transaction> transactionToUpdateOpt = findTransactionById(id);
        if (transactionToUpdateOpt.isPresent()) {
            Transaction oldTransaction = transactionToUpdateOpt.get();
            int index = transactions.indexOf(oldTransaction);

            // Create a new transaction with the same ID but new details.
            Transaction updatedTransaction = new Transaction(id, newDate, newDescription, newAmount, newType, newCategory);

            // Replace the old object with the new one at the same position.
            transactions.set(index, updatedTransaction);
            repository.saveTransactions(this.transactions);
            return true;
        }
        return false;
    }
}

