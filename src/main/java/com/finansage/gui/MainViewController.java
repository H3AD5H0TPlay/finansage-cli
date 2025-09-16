package com.finansage.gui;

import com.finansage.model.FinancialSummary;
import com.finansage.model.Transaction;
import com.finansage.model.TransactionType;
import com.finansage.service.TransactionService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

/**
 * The Controller for the main application view.
 * It handles the layout and user interactions.
 */
public class MainViewController {

    private final TransactionService transactionService;
    private final TableView<Transaction> transactionTable;
    private final ObservableList<Transaction> transactionData;

    public MainViewController(TransactionService transactionService) {
        this.transactionService = transactionService;
        // FIX: Initialize the data list BEFORE creating the table that uses it.
        this.transactionData = FXCollections.observableArrayList();
        this.transactionTable = createTransactionTable();
    }

    public BorderPane getView() {
        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(10));

        // Create the toolbar with action buttons
        ToolBar toolBar = createToolBar();
        layout.setTop(toolBar);

        // Set the table in the center
        layout.setCenter(transactionTable);
        BorderPane.setMargin(transactionTable, new Insets(10, 0, 0, 0));

        loadTransactionData();

        return layout;
    }

    private void loadTransactionData() {
        transactionData.setAll(transactionService.getAllTransactions());
    }

    private ToolBar createToolBar() {
        Button addButton = new Button("Add");
        addButton.setOnAction(e -> handleAddTransaction());

        Button editButton = new Button("Edit");
        editButton.setOnAction(e -> handleEditTransaction());

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> handleDeleteTransaction());

        Button summaryButton = new Button("Summary");
        summaryButton.setOnAction(e -> handleShowSummary());

        // A spacer to push buttons to the left and right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        ToolBar toolBar = new ToolBar(
                addButton,
                editButton,
                deleteButton,
                spacer,
                summaryButton
        );
        toolBar.setPadding(new Insets(5));
        return toolBar;
    }

    private void handleAddTransaction() {
        TransactionDialog dialog = new TransactionDialog();
        Optional<Transaction> result = dialog.showAndWait();

        result.ifPresent(transaction -> {
            transactionService.addTransaction(transaction);
            transactionData.add(transaction); // More efficient: directly update the UI list
        });
    }

    private void handleEditTransaction() {
        Transaction selectedTransaction = transactionTable.getSelectionModel().getSelectedItem();
        if (selectedTransaction == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a transaction in the table to edit.");
            return;
        }

        TransactionDialog dialog = new TransactionDialog(selectedTransaction);
        Optional<Transaction> result = dialog.showAndWait();

        result.ifPresent(updatedTransaction -> {
            if (transactionService.updateTransaction(updatedTransaction)) {
                // More efficient: find and replace the item in the UI list
                int index = transactionData.indexOf(selectedTransaction);
                if (index != -1) {
                    transactionData.set(index, updatedTransaction);
                }
            }
        });
    }

    private void handleDeleteTransaction() {
        Transaction selectedTransaction = transactionTable.getSelectionModel().getSelectedItem();
        if (selectedTransaction == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a transaction in the table to delete.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Delete Transaction");
        confirmation.setContentText("Are you sure you want to delete this transaction?\n" +
                selectedTransaction.getDescription() + " (" + selectedTransaction.getAmount() + ")");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (transactionService.deleteTransaction(selectedTransaction.getId())) {
                transactionData.remove(selectedTransaction); // More efficient: directly remove from the UI list
            }
        }
    }

    private void handleShowSummary() {
        FinancialSummary summary = transactionService.getFinancialSummary();
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("Financial Summary");
        info.setHeaderText("Here is your current financial summary:");
        info.setContentText(
                "Total Income:  " + summary.totalIncome() + "\n" +
                        "Total Expenses: " + summary.totalExpenses() + "\n\n" +
                        "Net Balance:   " + summary.netBalance()
        );
        info.showAndWait();
    }


    private TableView<Transaction> createTransactionTable() {
        TableView<Transaction> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        // Date Column
        TableColumn<Transaction, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setMinWidth(100);

        // Description Column
        TableColumn<Transaction, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descCol.setMinWidth(250);

        // Amount Column
        TableColumn<Transaction, BigDecimal> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountCol.setMinWidth(100);
        amountCol.setStyle("-fx-alignment: CENTER-RIGHT;");


        // Type Column
        TableColumn<Transaction, TransactionType> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setMinWidth(80);

        // Category Column
        TableColumn<Transaction, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryCol.setMinWidth(120);

        table.getColumns().addAll(dateCol, descCol, amountCol, typeCol, categoryCol);
        table.setItems(transactionData);
        return table;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

