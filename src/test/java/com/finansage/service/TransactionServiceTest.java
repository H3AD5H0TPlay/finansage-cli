package com.finansage.service;

import com.finansage.model.Transaction;
import com.finansage.model.TransactionType;
import com.finansage.repository.TransactionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    private TransactionService transactionService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        // Default setup: For tests that need a clean slate, the service will start with an empty list.
        when(transactionRepository.loadTransactions()).thenReturn(new ArrayList<>());
        transactionService = new TransactionService(transactionRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void deleteTransaction_shouldReturnTrue_whenTransactionExists() {
        // Arrange
        // We will create a service instance specifically for this test, pre-loaded with data.
        Transaction tx = new Transaction(LocalDate.now(), "Test", BigDecimal.TEN, TransactionType.INCOME, "Salary");
        List<Transaction> initialList = new ArrayList<>();
        initialList.add(tx);
        when(transactionRepository.loadTransactions()).thenReturn(initialList);
        transactionService = new TransactionService(transactionRepository); // Re-initialize with prepared data

        // Act
        boolean result = transactionService.deleteTransaction(tx.getId());

        // Assert
        assertTrue(result);
        assertTrue(transactionService.getAllTransactions().isEmpty());
        // Verification is now clean. Only deleteTransaction calls saveTransactions.
        verify(transactionRepository, times(1)).saveTransactions(anyList());
    }

    @Test
    void deleteTransaction_shouldReturnFalse_whenTransactionDoesNotExist() {
        // Arrange (uses the default empty setup from setUp())

        // Act
        boolean result = transactionService.deleteTransaction("non-existent-id");

        // Assert
        assertFalse(result);
        verify(transactionRepository, never()).saveTransactions(anyList());
    }

    @Test
    void findTransactionById_shouldReturnTransaction_whenIdExists() {
        // Arrange
        Transaction tx1 = new Transaction(LocalDate.now(), "Test 1", BigDecimal.TEN, TransactionType.INCOME, "Salary");
        List<Transaction> initialList = new ArrayList<>();
        initialList.add(tx1);
        when(transactionRepository.loadTransactions()).thenReturn(initialList);
        transactionService = new TransactionService(transactionRepository); // Re-initialize

        // Act
        Optional<Transaction> foundTxOpt = transactionService.findTransactionById(tx1.getId());

        // Assert
        assertTrue(foundTxOpt.isPresent());
        assertEquals(tx1.getId(), foundTxOpt.get().getId());
    }

    @Test
    void findTransactionById_shouldReturnEmpty_whenIdDoesNotExist() {
        // Arrange (uses the default empty setup)

        // Act
        Optional<Transaction> foundTxOpt = transactionService.findTransactionById("non-existent-id");

        // Assert
        assertTrue(foundTxOpt.isEmpty());
    }

    @Test
    void updateTransaction_shouldReplaceTransaction_whenIdExists() {
        // Arrange
        Transaction originalTx = new Transaction(LocalDate.now(), "Original", BigDecimal.TEN, TransactionType.INCOME, "Salary");
        List<Transaction> initialList = new ArrayList<>();
        initialList.add(originalTx);
        when(transactionRepository.loadTransactions()).thenReturn(initialList);
        transactionService = new TransactionService(transactionRepository); // Re-initialize with data

        String idToUpdate = originalTx.getId();
        LocalDate newDate = LocalDate.now().minusDays(1);
        String newDesc = "Updated";
        BigDecimal newAmount = BigDecimal.valueOf(100);
        TransactionType newType = TransactionType.EXPENSE;
        String newCat = "Groceries";

        // Act
        boolean result = transactionService.updateTransaction(idToUpdate, newDate, newDesc, newAmount, newType, newCat);

        // Assert
        assertTrue(result);
        assertEquals(1, transactionService.getAllTransactions().size());
        Transaction updatedTx = transactionService.getAllTransactions().get(0);
        assertEquals(idToUpdate, updatedTx.getId());
        assertEquals(newDesc, updatedTx.getDescription());
        assertEquals(0, newAmount.compareTo(updatedTx.getAmount()));

        ArgumentCaptor<List<Transaction>> listCaptor = ArgumentCaptor.forClass(List.class);
        // The mock is clean, so we can now correctly verify the single call from updateTransaction.
        verify(transactionRepository, times(1)).saveTransactions(listCaptor.capture());
        List<Transaction> savedList = listCaptor.getValue();
        assertEquals(1, savedList.size());
        assertEquals(newDesc, savedList.get(0).getDescription());
    }
}

