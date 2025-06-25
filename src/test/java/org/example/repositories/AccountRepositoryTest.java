package org.example.repositories;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.example.models.Account;
import org.example.models.enums.AccountType;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Account Management")
@Feature("Account data storage and search")
@DisplayName("Unit tests for AccountRepository")
public class AccountRepositoryTest {
    @TempDir
    Path tempDir;
    private AccountRepository accountRepository;

    @BeforeEach
    public void setUp() throws IOException {
        Path testAccountPath = tempDir.resolve("test_accounts.json");
        accountRepository = new AccountRepository(testAccountPath.toString());

        Files.write(testAccountPath, new ObjectMapper().writeValueAsBytes(new ArrayList<>()));
    }

    @Test
    @DisplayName("Saving and retrieving account by ID")
    public void testSaveAndGetAccountById() {
        Account account = new Account(AccountType.DEBIT);
        account.setBalance(1000.0);
        String id = account.getId();

        accountRepository.save(account);
        Optional<Account> found = accountRepository.getAccountById(id);

        assertTrue(found.isPresent());
        assertEquals(AccountType.DEBIT, found.get().getAccountType());
        assertEquals(1000.0, found.get().getBalance());
    }

    @Test
    @DisplayName("Updating an existing account")
    public void testUpdateAccount() {
        Account account = new Account(AccountType.DEBIT);
        account.setBalance(1000.0);
        String id = account.getId();
        accountRepository.save(account);

        account.setBalance(2000.0);
        accountRepository.save(account);
        Optional<Account> result = accountRepository.getAccountById(id);

        assertTrue(result.isPresent());
        assertEquals(2000.0, result.get().getBalance());
    }

    @Test
    @DisplayName("Retrieving a non-existent account")
    public void testGetNonExistentAccount() {
        Optional<Account> result = accountRepository.getAccountById("non-existent-id");
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Retrieving all accounts")
    public void testGetAllAccounts() {
        Account a1 = new Account();
        Account a2 = new Account();

        accountRepository.save(a1);
        accountRepository.save(a2);
        List<Account> allAccounts = accountRepository.getAllAccounts();

        assertEquals(2, allAccounts.size());
    }
}

