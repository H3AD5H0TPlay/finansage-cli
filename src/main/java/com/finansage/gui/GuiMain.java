package com.finansage.gui;

import com.finansage.repository.TransactionRepository;
import com.finansage.service.TransactionService;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * The main entry point for the FinanSage GUI application.
 */
public class GuiMain extends Application {

    @Override
    public void start(Stage primaryStage) {
        System.out.println("FinanSage GUI is starting...");

        // --- Backend Initialization ---
        final String DATA_FILE = "transactions.csv";
        TransactionRepository transactionRepository = new TransactionRepository(DATA_FILE);
        TransactionService transactionService = new TransactionService(transactionRepository);

        // --- Frontend Initialization ---
        MainViewController mainViewController = new MainViewController(transactionService);
        BorderPane root = mainViewController.getView();

        // --- Scene and Stage Setup ---
        Scene scene = new Scene(root, 800, 600);

        // Load and apply the CSS stylesheet for our dark theme
        try {
            String cssPath = Objects.requireNonNull(getClass().getResource("/styles/dark-theme.css")).toExternalForm();
            scene.getStylesheets().add(cssPath);
        } catch (NullPointerException e) {
            System.err.println("Error: Could not find stylesheet. Make sure 'dark-theme.css' is in the 'src/main/resources/styles' folder.");
        }


        primaryStage.setTitle("FinanSage - Your Personal Finance Manager");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

