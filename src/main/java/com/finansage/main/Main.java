package com.finansage.main;

import com.finansage.cli.CommandLineInterface;
import com.finansage.service.TransactionService;

public class Main {
    public static void main(String[] args) {
        System.out.println("Booting FinanSage...");

        TransactionService transactionService = new TransactionService();

        CommandLineInterface cli = new CommandLineInterface(transactionService);

        cli.run();
    }
}

