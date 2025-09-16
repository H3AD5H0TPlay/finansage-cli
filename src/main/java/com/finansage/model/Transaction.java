package com.finansage.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Represents a single financial transaction. This is an immutable data class.
 */
public final class Transaction {
    private final String id;
    private final LocalDate date;
    private final String description;
    private final BigDecimal amount;
    private final TransactionType type;
    private final String category;

    /**
     * Constructor for creating a brand new transaction. Generates a unique ID.
     */
    public Transaction(LocalDate date, String description, BigDecimal amount, TransactionType type, String category) {
        this(UUID.randomUUID().toString(), date, description, amount, type, category);
    }

    /**
     * Constructor for recreating a transaction from a data source (e.g., a file).
     */
    public Transaction(String id, LocalDate date, String description, BigDecimal amount, TransactionType type, String category) {
        this.id = id;
        this.date = date;
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.category = category;
    }

    // --- Public Getter Methods ---

    public String getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }
}

