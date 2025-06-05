package org.example.repositories;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.example.models.Transaction;
import org.example.models.enums.TransactionType;
import org.junit.jupiter.api.DisplayName;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Transactions")
@Feature("Transaction data storage and search")
@DisplayName("Unit tests for TransactionRepository")
public class TransactionRepositoryTest {
    private final String TEST_TRANSACTION_PATH = "database/test_transactions.json";
    private TransactionRepository transactionRepository;

    @BeforeEach
    public void setUp() throws IOException {
        transactionRepository = new TransactionRepository(TEST_TRANSACTION_PATH);
        File file = new File(TEST_TRANSACTION_PATH);
        boolean created = file.createNewFile();
        if (created) new ObjectMapper().writeValue(file, new ArrayList<>());
    }

    @AfterEach
    public void tearDown() {
        File file = new File(TEST_TRANSACTION_PATH);
        if (file.exists()) file.delete();
    }

    @Test
    @DisplayName("Saving and retrieving a transaction by ID")
    public void testSaveAndGetTransactionById() {
        Transaction tx = Transaction.builder()
                .type(TransactionType.TRANSFER)
                .amount(150.0)
                .timestamp(LocalDateTime.now())
                .sourceAccountId("acc-1")
                .targetAccountId("acc-2")
                .build();

        transactionRepository.save(tx);
        Optional<Transaction> found = transactionRepository.getTransactionById(tx.getId());

        assertTrue(found.isPresent());
        assertEquals(tx, found.get());
    }

    @Test
    @DisplayName("Saving a transaction with invalid data throws exception")
    public void testSaveInvalidTransaction_ShouldThrowException() {
        Transaction invalidTx = Transaction.builder()
                .type(null)
                .amount(null)
                .timestamp(LocalDateTime.now())
                .sourceAccountId("acc-1")
                .targetAccountId("acc-2")
                .build();

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> transactionRepository.save(invalidTx)
        );

        assertTrue(exception.getMessage().contains("Invalid transaction"));
    }

    @Test
    @DisplayName("Retrieving all transactions by sourceAccountId")
    public void testGetTransactionsBySourceId() {
        Transaction tx1 = Transaction.builder()
                .type(TransactionType.TRANSFER)
                .amount(100.0)
                .timestamp(LocalDateTime.now())
                .sourceAccountId("acc-123")
                .targetAccountId("acc-999")
                .build();

        Transaction tx2 = Transaction.builder()
                .type(TransactionType.WITHDRAW)
                .amount(50.0)
                .timestamp(LocalDateTime.now())
                .sourceAccountId("acc-123")
                .targetAccountId(null)
                .build();

        Transaction tx3 = Transaction.builder()
                .type(TransactionType.DEPOSIT)
                .amount(200.0)
                .timestamp(LocalDateTime.now())
                .sourceAccountId("another-source")
                .targetAccountId("acc-123")
                .build();

        transactionRepository.save(tx1);
        transactionRepository.save(tx2);
        transactionRepository.save(tx3);
        List<Transaction> results = transactionRepository.getAllTransactionsBySourceId("acc-123");

        assertEquals(2, results.size());
    }

    @Test
    @DisplayName("Retrieving all transactions")
    public void testGetAllTransactions() {
        Transaction tx1 = Transaction.builder()
                .type(TransactionType.DEPOSIT)
                .amount(300.0)
                .timestamp(LocalDateTime.now())
                .sourceAccountId("acc-A")
                .targetAccountId("acc-B")
                .build();

        Transaction tx2 = Transaction.builder()
                .type(TransactionType.WITHDRAW)
                .amount(75.0)
                .timestamp(LocalDateTime.now())
                .sourceAccountId("acc-A")
                .targetAccountId(null)
                .build();

        transactionRepository.save(tx1);
        transactionRepository.save(tx2);

        List<Transaction> all = transactionRepository.getAllTransactions();
        assertEquals(2, all.size());
    }

    @Test
    @DisplayName("Retrieving a transaction by non-existent ID")
    public void testGetTransactionByInvalidId() {
        Optional<Transaction> tx = transactionRepository.getTransactionById("non-existent-id");
        assertTrue(tx.isEmpty());
    }
}

