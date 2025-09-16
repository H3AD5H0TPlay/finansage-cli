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

/**
 * The "brain" of the application. Handles all business logic related to transactions.
 * It uses the TransactionRepository to load and save data.
 */
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final List<Transaction> transactions;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
        this.transactions = new ArrayList<>(this.transactionRepository.loadTransactions());
    }

    /**
     * Creates a new transaction from individual fields and saves it.
     * This method is kept for compatibility with the CLI.
     */
    public void addTransaction(LocalDate date, String description, BigDecimal amount, TransactionType type, String category) {
        Transaction newTransaction = new Transaction(date, description, amount, type, category);
        addTransaction(newTransaction);
    }

    /**
     * Adds a pre-constructed Transaction object and saves it.
     * This is the new, preferred method for the GUI.
     * @param transaction The transaction object to add.
     */
    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
        this.transactionRepository.saveTransactions(this.transactions);
    }

    public List<Transaction> getAllTransactions() {
        return new ArrayList<>(this.transactions); // Return a copy to prevent external modification
    }

    public boolean deleteTransaction(String id) {
        boolean removed = this.transactions.removeIf(transaction -> transaction.getId().equals(id));
        if (removed) {
            this.transactionRepository.saveTransactions(this.transactions);
        }
        return removed;
    }

    /**
     * Finds a transaction by its unique ID.
     * @param id The ID of the transaction to find.
     * @return An Optional containing the transaction if found, otherwise an empty Optional.
     */
    public Optional<Transaction> findTransactionById(String id) {
        return this.transactions.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst();
    }

    /**
     * Updates an existing transaction by replacing it with a new one.
     * This version is for the GUI, accepting a full Transaction object.
     * @param updatedTransaction The transaction object containing the new details.
     * @return true if the transaction was found and updated, false otherwise.
     */
    public boolean updateTransaction(Transaction updatedTransaction) {
        int index = -1;
        for (int i = 0; i < transactions.size(); i++) {
            if (transactions.get(i).getId().equals(updatedTransaction.getId())) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            transactions.set(index, updatedTransaction);
            transactionRepository.saveTransactions(transactions);
            return true;
        }
        return false;
    }

    /**
     * Overloaded update method for the CLI.
     * Creates a new Transaction object from individual fields and calls the main update method.
     * @return true if the transaction was found and updated, false otherwise.
     */
    public boolean updateTransaction(String id, LocalDate date, String description, BigDecimal amount, TransactionType type, String category) {
        Transaction updatedTransaction = new Transaction(id, date, description, amount, type, category);
        return updateTransaction(updatedTransaction);
    }


    public FinancialSummary getFinancialSummary() {
        BigDecimal totalIncome = transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpenses = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netBalance = totalIncome.subtract(totalExpenses);

        return new FinancialSummary(totalIncome, totalExpenses, netBalance);
    }
}

