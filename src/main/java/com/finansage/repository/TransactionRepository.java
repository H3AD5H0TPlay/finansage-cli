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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TransactionRepository {

    private final String csvFilePath;

    public TransactionRepository(String csvFilePath) {
        this.csvFilePath = csvFilePath;
    }

    public List<Transaction> loadTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        File file = new File(csvFilePath);

        if (!file.exists()) {
            return transactions;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length == 6) {
                    try {
                        String id = values[0];
                        LocalDate date = LocalDate.parse(values[1]);
                        String description = values[2];
                        BigDecimal amount = new BigDecimal(values[3]);
                        TransactionType type = TransactionType.valueOf(values[4]);
                        String category = values[5];
                        transactions.add(new Transaction(id, date, description, amount, type, category));
                    } catch (Exception e) {
                        System.err.println("Skipping malformed line in CSV: " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading transactions from file: " + e.getMessage());
        }
        return transactions;
    }

    public void saveTransactions(List<Transaction> transactions) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvFilePath))) {
            bw.write("id,date,description,amount,type,category");
            bw.newLine();
            for (Transaction transaction : transactions) {
                String line = String.join(",",
                        transaction.getId(),
                        transaction.getDate().toString(),
                        transaction.getDescription(),
                        transaction.getAmount().toPlainString(),
                        transaction.getType().name(),
                        transaction.getCategory()
                );
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving transactions to file: " + e.getMessage());
        }
    }
}
