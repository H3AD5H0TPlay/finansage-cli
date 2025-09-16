package com.finansage.gui;

import com.finansage.model.Transaction;
import com.finansage.model.TransactionType;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

/**
 * A reusable dialog for adding or editing a Transaction.
 */
public class TransactionDialog extends Dialog<Transaction> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final DatePicker datePicker = new DatePicker();
    private final TextField descriptionField = new TextField();
    private final TextField amountField = new TextField();
    private final ComboBox<TransactionType> typeComboBox = new ComboBox<>();
    private final TextField categoryField = new TextField();
    private final Transaction originalTransaction; // Used for editing

    /**
     * Constructor for creating a new transaction (Add mode).
     */
    public TransactionDialog() {
        this(null); // Call the main constructor with no original transaction
    }

    /**
     * Constructor for editing an existing transaction (Edit mode).
     * @param transaction The transaction to edit. If null, dialog is in Add mode.
     */
    public TransactionDialog(Transaction transaction) {
        this.originalTransaction = transaction;
        boolean isEditMode = (originalTransaction != null);

        setTitle(isEditMode ? "Edit Transaction" : "Add New Transaction");
        setHeaderText(isEditMode ? "Edit the details of the transaction." : "Enter the details for the new transaction.");

        setupControls();
        setupLayout();
        setupValidation();
        setupResultConverter();

        if (isEditMode) {
            prefillData();
        } else {
            // Set default values for a better user experience in Add mode
            datePicker.setValue(LocalDate.now());
            typeComboBox.setValue(TransactionType.EXPENSE);
        }

        // Request focus on the first input field
        Platform.runLater(datePicker::requestFocus);
    }


    private void setupControls() {
        datePicker.setPromptText("YYYY-MM-DD");
        datePicker.setConverter(new StringConverter<>() {
            @Override
            public String toString(LocalDate date) {
                return (date != null) ? DATE_FORMATTER.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                try {
                    return (string != null && !string.isEmpty()) ? LocalDate.parse(string, DATE_FORMATTER) : null;
                } catch (DateTimeParseException e) {
                    return null;
                }
            }
        });

        descriptionField.setPromptText("e.g., Groceries, Salary");
        amountField.setPromptText("e.g., 50.99");
        typeComboBox.getItems().addAll(TransactionType.values());
        categoryField.setPromptText("e.g., Food, Work");
    }

    private void setupLayout() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Date:"), 0, 0);
        grid.add(datePicker, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionField, 1, 1);
        grid.add(new Label("Amount:"), 0, 2);
        grid.add(amountField, 1, 2);
        grid.add(new Label("Type:"), 0, 3);
        grid.add(typeComboBox, 1, 3);
        grid.add(new Label("Category:"), 0, 4);
        grid.add(categoryField, 1, 4);

        getDialogPane().setContent(grid);

        ButtonType okButtonType = ButtonType.OK;
        getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);
    }

    private void setupValidation() {
        Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);

        // Validation logic to enable/disable the OK button
        okButton.disableProperty().bind(
                Bindings.createBooleanBinding(() -> {
                            boolean isDateInvalid = datePicker.getValue() == null;
                            boolean isDescriptionEmpty = descriptionField.getText().trim().isEmpty();
                            boolean isAmountInvalid;
                            try {
                                new BigDecimal(amountField.getText());
                                isAmountInvalid = false;
                            } catch (NumberFormatException e) {
                                isAmountInvalid = true;
                            }
                            boolean isTypeNotSelected = typeComboBox.getValue() == null;
                            boolean isCategoryEmpty = categoryField.getText().trim().isEmpty();

                            return isDateInvalid || isDescriptionEmpty || isAmountInvalid || isTypeNotSelected || isCategoryEmpty;
                        },
                        datePicker.valueProperty(),
                        descriptionField.textProperty(),
                        amountField.textProperty(),
                        typeComboBox.valueProperty(),
                        categoryField.textProperty())
        );
    }

    private void prefillData() {
        datePicker.setValue(originalTransaction.getDate());
        descriptionField.setText(originalTransaction.getDescription());
        amountField.setText(originalTransaction.getAmount().toPlainString());
        typeComboBox.setValue(originalTransaction.getType());
        categoryField.setText(originalTransaction.getCategory());
    }

    private void setupResultConverter() {
        setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                LocalDate date = datePicker.getValue();
                String description = descriptionField.getText().trim();
                BigDecimal amount = new BigDecimal(amountField.getText());
                TransactionType type = typeComboBox.getValue();
                String category = categoryField.getText().trim();

                // If we are editing, use the original ID. Otherwise, it's a new transaction.
                if (originalTransaction != null) {
                    return new Transaction(originalTransaction.getId(), date, description, amount, type, category);
                } else {
                    return new Transaction(date, description, amount, type, category);
                }
            }
            return null;
        });
    }
}

