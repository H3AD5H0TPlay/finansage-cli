package com.finansage.gui;

import com.finansage.model.FinancialSummary;
import com.finansage.model.Transaction;
import com.finansage.service.TransactionService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class MainViewController {

    private final TransactionService transactionService;
    private final TableView<Transaction> transactionTable;
    private final ObservableList<Transaction> observableTransactions;

    public MainViewController(TransactionService transactionService) {
        this.transactionService = transactionService;
        this.observableTransactions = FXCollections.observableArrayList(transactionService.getAllTransactions());
        this.transactionTable = createTransactionTable();
    }

    public BorderPane getView() {
        BorderPane mainLayout = new BorderPane();

        // Left Sidebar for navigation
        VBox sidebar = createSidebar();
        mainLayout.setLeft(sidebar);

        // Main content area
        BorderPane contentPane = createContentPane();
        mainLayout.setCenter(contentPane);

        return mainLayout;
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPadding(new Insets(20, 10, 20, 10));

        // Placeholder buttons inspired by the screenshot
        Button dashboardButton = new Button("Dashboard");
        Button transactionsButton = new Button("Transactions");
        Button financeButton = new Button("Finance");
        Button settingsButton = new Button("Settings");

        // Apply styles
        dashboardButton.getStyleClass().add("sidebar-button");
        transactionsButton.getStyleClass().addAll("sidebar-button", "sidebar-button-selected"); // Mark 'Transactions' as selected
        financeButton.getStyleClass().add("sidebar-button");
        settingsButton.getStyleClass().add("sidebar-button");

        sidebar.getChildren().addAll(dashboardButton, transactionsButton, financeButton, settingsButton);

        return sidebar;
    }

    private BorderPane createContentPane() {
        BorderPane contentPane = new BorderPane();
        contentPane.setPadding(new Insets(20));

        // Top toolbar
        HBox toolbar = createToolbar();
        contentPane.setTop(toolbar);

        // Table in the center
        contentPane.setCenter(transactionTable);
        BorderPane.setMargin(transactionTable, new Insets(20, 0, 0, 0));

        return contentPane;
    }


    private HBox createToolbar() {
        // --- Buttons ---
        Button addButton = new Button("Add Transaction");
        Button editButton = new Button("Edit");
        Button deleteButton = new Button("Delete");
        Button summaryButton = new Button("View Summary");
        summaryButton.setId("summary-button"); // Primary action button

        addButton.setOnAction(e -> handleAddTransaction());
        editButton.setOnAction(e -> handleEditTransaction());
        deleteButton.setOnAction(e -> handleDeleteTransaction());
        summaryButton.setOnAction(e -> handleShowSummary());


        // --- Layout ---
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox toolbar = new HBox(10, addButton, editButton, deleteButton, spacer, summaryButton);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        return toolbar;
    }

    private TableView<Transaction> createTransactionTable() {
        TableView<Transaction> table = new TableView<>(observableTransactions);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setPlaceholder(new Label("No transactions found. Click 'Add Transaction' to get started."));

        // --- Columns ---
        TableColumn<Transaction, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        dateCol.setPrefWidth(100);
        dateCol.setMinWidth(100);


        TableColumn<Transaction, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        descCol.setPrefWidth(250);

        TableColumn<Transaction, String> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(cellData -> {
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
            return new SimpleStringProperty(currencyFormat.format(cellData.getValue().getAmount()));
        });
        amountCol.setStyle("-fx-alignment: CENTER-RIGHT;");
        amountCol.setPrefWidth(120);
        amountCol.setMinWidth(120);

        TableColumn<Transaction, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getType().toString()));
        typeCol.setPrefWidth(100);
        typeCol.setMinWidth(100);

        TableColumn<Transaction, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategory()));
        categoryCol.setPrefWidth(150);
        categoryCol.setMinWidth(150);

        table.getColumns().setAll(dateCol, descCol, amountCol, typeCol, categoryCol);

        return table;
    }

    // --- Action Handlers (Unchanged from previous version) ---

    private void handleAddTransaction() {
        TransactionDialog dialog = new TransactionDialog();
        Optional<Transaction> result = dialog.showAndWait();

        result.ifPresent(newTransaction -> {
            transactionService.addTransaction(newTransaction);
            observableTransactions.add(newTransaction);
        });
    }

    private void handleEditTransaction() {
        Transaction selectedTransaction = transactionTable.getSelectionModel().getSelectedItem();
        if (selectedTransaction == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a transaction to edit.");
            return;
        }

        TransactionDialog dialog = new TransactionDialog(selectedTransaction);
        Optional<Transaction> result = dialog.showAndWait();

        result.ifPresent(updatedTransaction -> {
            if (transactionService.updateTransaction(updatedTransaction)) {
                int index = observableTransactions.indexOf(selectedTransaction);
                if (index != -1) {
                    observableTransactions.set(index, updatedTransaction);
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Update Failed", "Could not update the transaction.");
            }
        });
    }

    private void handleDeleteTransaction() {
        Transaction selectedTransaction = transactionTable.getSelectionModel().getSelectedItem();
        if (selectedTransaction == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a transaction to delete.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Delete Transaction");
        confirmAlert.setContentText("Are you sure you want to delete this transaction?\n" + selectedTransaction.getDescription());

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (transactionService.deleteTransaction(selectedTransaction.getId())) {
                observableTransactions.remove(selectedTransaction);
            } else {
                showAlert(Alert.AlertType.ERROR, "Delete Failed", "Could not delete the selected transaction.");
            }
        }
    }

    private void handleShowSummary() {
        FinancialSummary summary = transactionService.getFinancialSummary();
        Alert summaryAlert = new Alert(Alert.AlertType.INFORMATION);
        summaryAlert.setTitle("Financial Summary");
        summaryAlert.setHeaderText("Your Current Financial Overview");

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        String content = String.format(
                "Total Income: \t%s\nTotal Expenses: \t%s\n\nNet Balance: \t%s",
                currencyFormat.format(summary.totalIncome()),
                currencyFormat.format(summary.totalExpenses()),
                currencyFormat.format(summary.netBalance())
        );

        summaryAlert.setContentText(content);
        // Apply the dark theme to the dialog pane
        DialogPane dialogPane = summaryAlert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/styles/dark-theme.css").toExternalForm());
        dialogPane.getStyleClass().add("dialog-pane");

        summaryAlert.showAndWait();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        // Apply the dark theme to the dialog pane
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/styles/dark-theme.css").toExternalForm());
        dialogPane.getStyleClass().add("dialog-pane");
        alert.showAndWait();
    }
}

