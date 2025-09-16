package com.finansage.gui;

import com.finansage.model.Transaction;
import com.finansage.model.TransactionType;
import com.finansage.service.TransactionService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Controller for the main application view. Manages the display and interaction of transactions.
 */
public class MainViewController {

    private final TransactionService transactionService;
    private final TableView<Transaction> transactionTable;
    private final ObservableList<Transaction> transactionData;

    // The main view pane managed by this controller
    private final BorderPane view;

    public MainViewController(TransactionService transactionService) {
        this.transactionService = transactionService;
        this.transactionData = FXCollections.observableArrayList();
        this.transactionTable = createTransactionTable();
        this.view = createMainView(); // Create the main view
        loadTransactionData();
    }

    private BorderPane createMainView() {
        BorderPane mainPane = new BorderPane();
        mainPane.setPadding(new Insets(10));

        // Create the action buttons
        Button addButton = new Button("Add");
        Button editButton = new Button("Edit");
        Button deleteButton = new Button("Delete");

        // Add placeholder actions for now
        addButton.setOnAction(event -> System.out.println("Add button clicked!"));
        editButton.setOnAction(event -> System.out.println("Edit button clicked!"));
        deleteButton.setOnAction(event -> System.out.println("Delete button clicked!"));

        ToolBar toolBar = new ToolBar(addButton, editButton, deleteButton);
        toolBar.setPadding(new Insets(0, 0, 10, 0)); // Add some spacing below the toolbar

        mainPane.setTop(toolBar);
        mainPane.setCenter(transactionTable);

        return mainPane;
    }

    private TableView<Transaction> createTransactionTable() {
        TableView<Transaction> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TableColumn<Transaction, LocalDate> dateCol = new TableColumn<>("Date");
        TableColumn<Transaction, String> descriptionCol = new TableColumn<>("Description");
        TableColumn<Transaction, BigDecimal> amountCol = new TableColumn<>("Amount");
        TableColumn<Transaction, TransactionType> typeCol = new TableColumn<>("Type");
        TableColumn<Transaction, String> categoryCol = new TableColumn<>("Category");

        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));

        table.getColumns().addAll(dateCol, descriptionCol, amountCol, typeCol, categoryCol);
        table.setItems(transactionData);

        // Professional Touch: Set relative column widths
        dateCol.setMaxWidth(1f * Integer.MAX_VALUE * 15); // 15%
        descriptionCol.setMaxWidth(1f * Integer.MAX_VALUE * 40); // 40%
        amountCol.setMaxWidth(1f * Integer.MAX_VALUE * 15); // 15%
        typeCol.setMaxWidth(1f * Integer.MAX_VALUE * 10); // 10%
        categoryCol.setMaxWidth(1f * Integer.MAX_VALUE * 20); // 20%

        return table;
    }

    public void loadTransactionData() {
        transactionData.clear();
        transactionData.addAll(transactionService.getAllTransactions());
    }

    /**
     * Exposes the main view component so it can be added to the main scene.
     * @return A BorderPane containing all the main view elements.
     */
    public BorderPane getView() {
        return view;
    }
}
