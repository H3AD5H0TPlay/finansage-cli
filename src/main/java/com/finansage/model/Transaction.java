package com.finansage.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class Transaction {
    private final String id;
    private final LocalDate date;
    private final String description;
    private final BigDecimal amount;
    private final TransactionType type;
    private final String category;

    /**
     * Constructor for creating a NEW transaction.
     * It automatically generates a unique ID.
     */
    public Transaction(LocalDate date, String description, BigDecimal amount, TransactionType type, String category) {
        this.id = UUID.randomUUID().toString(); // Auto-generate the ID
        this.date = date;
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.category = category;
    }

    /**
     * Constructor for LOADING an existing transaction from a data source.
     * It uses the provided ID.
     */
    public Transaction(String id, LocalDate date, String description, BigDecimal amount, TransactionType type, String category) {
        this.id = id;
        this.date = date;
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.category = category;
    }

    // --- Getters ---

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

