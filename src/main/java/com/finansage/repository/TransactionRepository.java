package com.finansage.repository;

import com.finansage.model.Transaction;
import com.finansage.model.TransactionType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TransactionRepository {
    private final String fileName;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public TransactionRepository(String fileName) {
        this.fileName = fileName;
    }

    public List<Transaction> loadTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        File file = new File(fileName);

        if (!file.exists()) {
            return transactions; // Return empty list if file doesn't exist yet
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // THE FIX: Read and discard the header line before the loop.
            String header = reader.readLine();
            if (header == null) {
                return transactions; // File is empty or only has a header.
            }

            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length == 6) {
                    // Re-create the transaction object using the full constructor
                    Transaction transaction = new Transaction(
                            values[0], // ID
                            LocalDate.parse(values[1], DATE_FORMATTER),
                            values[2], // Description
                            new BigDecimal(values[3]),
                            TransactionType.valueOf(values[4]),
                            values[5] // Category
                    );
                    transactions.add(transaction);
                }
            }
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Error loading transactions: " + e.getMessage());
        }
        return transactions;
    }

    public void saveTransactions(List<Transaction> transactions) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("ID,Date,Description,Amount,Type,Category");
            writer.newLine();

            for (Transaction tx : transactions) {
                String line = String.join(",",
                        tx.getId(),
                        tx.getDate().format(DATE_FORMATTER),
                        tx.getDescription(),
                        tx.getAmount().toPlainString(),
                        tx.getType().name(),
                        tx.getCategory()
                );
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving transactions: " + e.getMessage());
        }
    }
}

