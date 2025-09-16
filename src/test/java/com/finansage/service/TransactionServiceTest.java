package com.finansage.service;

import com.finansage.model.Transaction;
import com.finansage.model.TransactionType;
import com.finansage.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private TransactionRepository mockRepository;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deleteTransaction_shouldReturnTrueAndSaveChanges_whenTransactionExists() {
        String idToDelete = UUID.randomUUID().toString();
        Transaction transaction1 = new Transaction(idToDelete, LocalDate.now(), "Test 1", BigDecimal.TEN, TransactionType.INCOME, "Test");
        Transaction transaction2 = new Transaction(UUID.randomUUID().toString(), LocalDate.now(), "Test 2", BigDecimal.ONE, TransactionType.EXPENSE, "Test");

        List<Transaction> initialTransactions = new ArrayList<>(List.of(transaction1, transaction2));

        when(mockRepository.loadTransactions()).thenReturn(initialTransactions);

        transactionService = new TransactionService(mockRepository);

        boolean result = transactionService.deleteTransaction(idToDelete);

        assertTrue(result, "The method should return true when a transaction is deleted.");
        assertEquals(1, transactionService.getAllTransactions().size(), "The transaction list should have one less item.");

        verify(mockRepository, times(1)).saveTransactions(anyList());
    }

    @Test
    void deleteTransaction_shouldReturnFalseAndNotSaveChanges_whenTransactionDoesNotExist() {
        Transaction transaction = new Transaction(UUID.randomUUID().toString(), LocalDate.now(), "Test 1", BigDecimal.TEN, TransactionType.INCOME, "Test");
        List<Transaction> initialTransactions = new ArrayList<>(List.of(transaction));
        when(mockRepository.loadTransactions()).thenReturn(initialTransactions);
        transactionService = new TransactionService(mockRepository);

        String nonExistentId = "non-existent-id";

        boolean result = transactionService.deleteTransaction(nonExistentId);

        assertFalse(result, "The method should return false for a non-existent ID.");
        assertEquals(1, transactionService.getAllTransactions().size(), "The transaction list size should be unchanged.");

        verify(mockRepository, never()).saveTransactions(anyList());
    }
}
