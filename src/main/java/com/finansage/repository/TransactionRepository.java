package com.finansage.repository;

import com.finansage.model.Transaction;
import com.finansage.model.TransactionType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TransactionRepository {

    private final Path filePath;

    public TransactionRepository(String fileName) {
        this.filePath = Paths.get(fileName);
    }

    public List<Transaction> loadTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return transactions;
        }

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 6) {
                    String id = parts[0];
                    LocalDate date = LocalDate.parse(parts[1]);
                    String description = parts[2];
                    BigDecimal amount = new BigDecimal(parts[3]);
                    TransactionType type = TransactionType.valueOf(parts[4]);
                    String category = parts[5];
                    transactions.add(new Transaction(id, date, description, amount, type, category));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading transactions from file: " + e.getMessage());
        }
        return transactions;
    }

    public void saveTransactions(List<Transaction> transactions) {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (Transaction t : transactions) {
                if (t.getId() == null) {}
                String line = String.join(",",
                        t.getId(),
                        t.getDate().toString(),
                        t.getDescription(),
                        t.getAmount().toPlainString(),
                        t.getType().name(),
                        t.getCategory()
                );
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving transactions to file: " + e.getMessage());
        }
    }
}
