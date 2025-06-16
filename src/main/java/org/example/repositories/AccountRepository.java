package org.example.repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.models.Account;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccountRepository {
    private final String ACCOUNT_PATH;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public AccountRepository() {
        ACCOUNT_PATH = "database/accounts.json";
    }

    public AccountRepository(String ACCOUNT_PATH) {
        this.ACCOUNT_PATH = ACCOUNT_PATH;
    }

    public void save(Account account) {
        List<Account> accounts = getAllAccounts();
        accounts.stream()
                .filter(a -> a.getId().equals(account.getId()))
                .findFirst()
                .ifPresent(accounts::remove);
        accounts.add(account);
        saveAll(accounts);
    }

    public void saveAll(List<Account> accounts) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(ACCOUNT_PATH), accounts);
        } catch (IOException e) {
            System.err.println("Error writing accounts list to file: " + e.getMessage());
        }
    }

    public List<Account> getAllAccounts() {
        try {
            File file = new File(ACCOUNT_PATH);
            if (!file.exists()) return new ArrayList<>();
            return objectMapper.readValue(file, new TypeReference<List<Account>>() {});
        } catch (IOException e) {
            System.err.println("Error receiving accounts from file: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Optional<Account> getAccountById(String accountId) {
        return getAllAccounts().stream()
                .filter(account -> account.getId().equals(accountId))
                .findFirst();
    }
}