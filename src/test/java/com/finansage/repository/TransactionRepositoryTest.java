package com.finansage.repository;

import com.finansage.model.Transaction;
import com.finansage.model.TransactionType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionRepositoryTest {

    private static final String TEST_FILE = "test_transactions.csv";
    private TransactionRepository repository;
    private Path testFilePath;

    @BeforeEach
    void setUp() {
        repository = new TransactionRepository(TEST_FILE);
        testFilePath = Paths.get(TEST_FILE);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(testFilePath);
    }

    @Test
    void saveAndLoadTransactions_ShouldPersistAndRetrieveDataCorrectly() {
        Transaction t1 = new Transaction(null, LocalDate.of(2024, 1, 15), "Salary", new BigDecimal("5000.00"), TransactionType.INCOME, "Work");
        Transaction t2 = new Transaction(null, LocalDate.of(2024, 1, 16), "Groceries", new BigDecimal("150.75"), TransactionType.EXPENSE, "Food");
        List<Transaction> originalTransactions = Arrays.asList(t1, t2);

        repository.saveTransactions(originalTransactions);
        List<Transaction> loadedTransactions = repository.loadTransactions();

        assertNotNull(loadedTransactions, "Loaded transactions list should not be null.");
        assertEquals(2, loadedTransactions.size(), "Should load exactly two transactions.");

        Transaction loadedT1 = loadedTransactions.getFirst();
        assertEquals(t1.getDate(), loadedT1.getDate());
        assertEquals(t1.getDescription(), loadedT1.getDescription());

        assertEquals(0, t1.getAmount().compareTo(loadedT1.getAmount()));
        assertEquals(t1.getType(), loadedT1.getType());
        assertEquals(t1.getCategory(), loadedT1.getCategory());
    }

    @Test
    void loadTransactions_WhenFileDoesNotExist_ShouldReturnEmptyList() {
        List<Transaction> loadedTransactions = repository.loadTransactions();

        assertNotNull(loadedTransactions, "The list should not be null even if the file doesn't exist.");
        assertTrue(loadedTransactions.isEmpty(), "The list should be empty when the file doesn't exist.");
    }
}
