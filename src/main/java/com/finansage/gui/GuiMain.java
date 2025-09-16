package com.finansage.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * The main entry point for the FinanSage JavaFX GUI application.
 */
public class GuiMain extends Application {

    @Override
    public void start(Stage primaryStage) {
        // A Stage is the main window of the application.

        // Create a simple UI component.
        Label welcomeLabel = new Label("FinanSage GUI is starting...");

        // A StackPane is a simple layout container.
        StackPane root = new StackPane();
        root.getChildren().add(welcomeLabel);

        // A Scene is the content inside the window. We set our layout pane as its root.
        Scene scene = new Scene(root, 640, 480);

        // Configure and show the window (Stage).
        primaryStage.setTitle("FinanSage - Personal Finance Manager");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        // This is the standard way to launch a JavaFX application.
        launch(args);
    }
}
