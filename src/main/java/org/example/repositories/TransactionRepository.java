package org.example.repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.models.Transaction;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TransactionRepository {
    private final String TRANSACTION_PATH = "database/transactions.json";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    static {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public void save(Transaction transaction) {
        List<Transaction> transactions = getAllTransactions();
        transactions.add(transaction);
        saveAll(transactions);
    }

    private void saveAll(List<Transaction> transactions) {
        if (transactions.stream().anyMatch(t -> t.getType() == null || t.getAmount() == null)) {
            throw new IllegalStateException("Invalid transaction data detected!");
        }
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(TRANSACTION_PATH), transactions);
        } catch (IOException io) {
            System.err.println("Error writing transactions list to file: " + io.getMessage());
        }
    }

    public List<Transaction> getAllTransactions() {
        try {
            File file = new File(TRANSACTION_PATH);
            if (!file.exists()) return new ArrayList<>();
            return objectMapper.readValue(file, new TypeReference<List<Transaction>>() {
            });
        } catch (IOException e) {
            System.err.println("Error reading transactions from file: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Optional<Transaction> getTransactionById(String id) {
        return getAllTransactions().stream()
                .filter(transaction -> transaction.getId().equals(id))
                .findFirst();
    }

    public List<Transaction> getAllTransactionsBySourceId(String targetId) {
        return getAllTransactions().stream()
                .filter(transaction -> transaction.getSourceAccountId().equals(targetId))
                .collect(Collectors.toList());
    }
}