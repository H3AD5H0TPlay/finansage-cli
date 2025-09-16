package com.finansage.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public final class Transaction {

    private final String id;
    private final LocalDate date;
    private final String description;
    private final BigDecimal amount;
    private final TransactionType type;
    private final String category;

    public Transaction(String id, LocalDate date, String description, BigDecimal amount, TransactionType type, String category) {
        if (id == null || id.isBlank()) {
            this.id = UUID.randomUUID().toString();
        } else {
            this.id = id;
        }
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

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", date=" + date +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", type=" + type +
                ", category='" + category + '\'' +
                '}';
    }
}

