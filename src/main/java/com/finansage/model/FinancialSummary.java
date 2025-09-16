package com.finansage.model;

import java.math.BigDecimal;

public record FinancialSummary(
        BigDecimal totalIncome,
        BigDecimal totalExpenses,
        BigDecimal netBalance
) {
}