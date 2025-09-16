package com.finansage.gui;

import com.finansage.model.Transaction;
import com.finansage.model.TransactionType;
import com.finansage.service.TransactionService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Controller for the main application view. Manages the display of transactions.
 */
public class MainViewController {

    private final TransactionService transactionService;
    private final TableView<Transaction> transactionTable;
    private final ObservableList<Transaction> transactionData;

    public MainViewController(TransactionService transactionService) {
        this.transactionService = transactionService;
        this.transactionData = FXCollections.observableArrayList();
        this.transactionTable = createTransactionTable();
        loadTransactionData();
    }

    /**
     * Creates and configures the main TableView for displaying transactions.
     * @return A fully configured TableView.
     */
    private TableView<Transaction> createTransactionTable() {
        TableView<Transaction> table = new TableView<>();

        // 1. Create columns
        TableColumn<Transaction, LocalDate> dateCol = new TableColumn<>("Date");
        TableColumn<Transaction, String> descriptionCol = new TableColumn<>("Description");
        TableColumn<Transaction, BigDecimal> amountCol = new TableColumn<>("Amount");
        TableColumn<Transaction, TransactionType> typeCol = new TableColumn<>("Type");
        TableColumn<Transaction, String> categoryCol = new TableColumn<>("Category");
        TableColumn<Transaction, String> idCol = new TableColumn<>("ID");

        // 2. Specify how to populate the columns from the Transaction model.
        // The string ("id", "date", etc.) MUST match the property name in the Transaction record.
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        // 3. Add columns to the table
        table.getColumns().addAll(dateCol, descriptionCol, amountCol, typeCol, categoryCol, idCol);

        // 4. Set the data source for the table
        table.setItems(transactionData);

        // Professional Touch: Set column widths to be a percentage of the table width
        dateCol.prefWidthProperty().bind(table.widthProperty().multiply(0.15));
        descriptionCol.prefWidthProperty().bind(table.widthProperty().multiply(0.35));
        amountCol.prefWidthProperty().bind(table.widthProperty().multiply(0.15));
        typeCol.prefWidthProperty().bind(table.widthProperty().multiply(0.10));
        categoryCol.prefWidthProperty().bind(table.widthProperty().multiply(0.25));


        return table;
    }

    /**
     * Loads transactions from the service and populates the observable list,
     * which automatically updates the TableView.
     */
    public void loadTransactionData() {
        transactionData.clear();
        transactionData.addAll(transactionService.getAllTransactions());
    }

    /**
     * Exposes the main view component (the table wrapped in a layout pane)
     * so it can be added to the main scene.
     * @return A VBox containing the transaction table.
     */
    public VBox getView() {
        VBox vbox = new VBox(transactionTable);
        vbox.setPadding(new Insets(10));
        return vbox;
    }
}
