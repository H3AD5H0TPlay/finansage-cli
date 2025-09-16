package com.finansage.gui;

import com.finansage.model.Transaction;
import com.finansage.model.TransactionType;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * A custom dialog for adding a new transaction.
 * This class encapsulates the entire form for creating a transaction.
 */
public class TransactionDialog extends Dialog<Transaction> {

    private final DatePicker datePicker = new DatePicker(LocalDate.now());
    private final TextField descriptionField = new TextField();
    private final TextField amountField = new TextField();
    private final ComboBox<TransactionType> typeComboBox = new ComboBox<>();
    private final TextField categoryField = new TextField();

    public TransactionDialog() {
        setTitle("Add New Transaction");
        initOwner(null);
        initModality(Modality.APPLICATION_MODAL); // Block interaction with the main window

        // Set up the content of the dialog
        getDialogPane().setContent(createForm());
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Enable/disable the OK button based on validation
        final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(true);
        amountField.textProperty().addListener((obs, oldVal, newVal) -> validate(okButton));
        descriptionField.textProperty().addListener((obs, oldVal, newVal) -> validate(okButton));
        categoryField.textProperty().addListener((obs, oldVal, newVal) -> validate(okButton));

        // This is the "converter" - it tells the dialog how to create a Transaction object
        // when the OK button is pressed.
        setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new Transaction(
                        datePicker.getValue(),
                        descriptionField.getText(),
                        new BigDecimal(amountField.getText()),
                        typeComboBox.getValue(),
                        categoryField.getText()
                );
            }
            return null;
        });
    }

    private GridPane createForm() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Configure input fields
        typeComboBox.getItems().setAll(TransactionType.values());
        typeComboBox.setValue(TransactionType.EXPENSE); // Default value

        // Professional Touch: Use a prompt text to guide the user
        descriptionField.setPromptText("e.g., Groceries");
        amountField.setPromptText("e.g., 50.75");
        categoryField.setPromptText("e.g., Food");

        // Layout the form
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

        return grid;
    }

    private void validate(Button okButton) {
        // Simple validation: Ensure required fields are not empty and amount is a valid number.
        boolean isAmountValid = false;
        try {
            BigDecimal amount = new BigDecimal(amountField.getText());
            isAmountValid = amount.compareTo(BigDecimal.ZERO) > 0;
        } catch (NumberFormatException e) {
            isAmountValid = false;
        }

        boolean isDescriptionValid = !descriptionField.getText().trim().isEmpty();
        boolean isCategoryValid = !categoryField.getText().trim().isEmpty();

        okButton.setDisable(!isAmountValid || !isDescriptionValid || !isCategoryValid);
    }

    /**
     * A static factory method to show the dialog and get the result.
     * This is a clean way to use the dialog from our controller.
     * @return An Optional containing the new Transaction if created, or an empty Optional if canceled.
     */
    public static Optional<Transaction> showAddTransactionDialog() {
        TransactionDialog dialog = new TransactionDialog();
        return dialog.showAndWait();
    }
}
