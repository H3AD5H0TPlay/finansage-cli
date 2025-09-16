package com.finansage.gui;

import com.finansage.repository.TransactionRepository;
import com.finansage.service.TransactionService;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * The main entry point for the FinanSage JavaFX GUI application.
 * This class is responsible for initializing the application layers and showing the main window.
 */
public class GuiMain extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 1. Initialize the backend (Model)
        final String DATA_FILE = "transactions.csv";
        TransactionRepository transactionRepository = new TransactionRepository(DATA_FILE);
        TransactionService transactionService = new TransactionService(transactionRepository);

        // 2. Initialize the Controller, injecting the service (Model)
        MainViewController mainViewController = new MainViewController(transactionService);

        // 3. Get the main view from the controller
        BorderPane root = mainViewController.getView();

        // 4. Create the Scene and show the Stage
        Scene scene = new Scene(root, 800, 600);

        primaryStage.setTitle("FinanSage - Personal Finance Manager");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}