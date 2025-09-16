package com.finansage.main;

import com.finansage.cli.CommandLineInterface;
import com.finansage.repository.TransactionRepository;
import com.finansage.service.TransactionService;

public class Main {
    public static void main(String[] args) {
        System.out.println("Booting FinanSage...");

        // Define the path for our data file.
        final String DATA_FILE = "transactions.csv";

        // 1. Initialize the Repository Layer (the "hands")
        TransactionRepository transactionRepository = new TransactionRepository(DATA_FILE);

        // 2. Initialize the Service Layer (the "brain"), injecting the repository.
        TransactionService transactionService = new TransactionService(transactionRepository);

        // 3. Initialize the UI Layer (the "face")
        CommandLineInterface cli = new CommandLineInterface(transactionService);

        // 4. Start the application
        cli.run();
    }
}

