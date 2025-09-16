package com.finansage.model;

import java.math.BigDecimal;

/**
 * A Data Transfer Object (DTO) representing a summary of financial transactions.
 * Using a record is a modern, concise way to create an immutable data carrier.
 * @param totalIncome The sum of all INCOME transactions.
 * @param totalExpenses The sum of all EXPENSE transactions.
 * @param netBalance The difference between income and expenses.
 */
public record FinancialSummary(
        BigDecimal totalIncome,
        BigDecimal totalExpenses,
        BigDecimal netBalance
) {
}

