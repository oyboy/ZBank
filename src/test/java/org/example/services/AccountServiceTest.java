package org.example.services;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.example.models.Account;
import org.example.models.User;
import org.example.models.enums.AccountType;
import org.example.repositories.AccountRepository;
import org.example.repositories.UserRepository;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Epic("Account Management")
@Feature("Account creation and search")
@DisplayName("Unit tests for AccountService")
public class AccountServiceTest {
    @Mock
    private AccountRepository mockAccountRepository;
    @Mock
    private UserRepository mockUserRepository;

    private AccountService accountService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        accountService = new AccountService(mockAccountRepository, mockUserRepository);
    }

    @Test
    @DisplayName("Creating a new account should save it and update the user")
    public void testCreateAccount_ShouldSaveAccountAndUpdateUser() {
        User user = new User("John Doe", "johndoe@example.com", "pass");
        Account account = new Account(AccountType.DEBIT);

        when(mockAccountRepository.getAccountById(account.getId())).thenReturn(Optional.of(account));
        accountService.createAccount(user, AccountType.DEBIT);

        assertFalse(user.getAccountIds().isEmpty());
        verify(mockAccountRepository, times(1)).save(any(Account.class));
        verify(mockUserRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Getting user's list of accounts")
    public void testGetUserAccounts_ShouldReturnListOfAccounts() {
        Account account1 = new Account(AccountType.DEBIT);
        Account account2 = new Account(AccountType.DEBIT);
        User user = new User("John Doe", "johndoe@example.com", "pass");
        user.setAccountIds(List.of(account1.getId(), account2.getId()));

        when(mockAccountRepository.getAccountById(account1.getId())).thenReturn(Optional.of(account1));
        when(mockAccountRepository.getAccountById(account2.getId())).thenReturn(Optional.of(account2));
        List<Account> accounts = accountService.getUserAccounts(user);

        assertEquals(2, accounts.size());
        assertTrue(accounts.contains(account1));
        assertTrue(accounts.contains(account2));
    }

    @Test
    @DisplayName("If one of the accounts is not found — it should not be included in the result")
    public void testGetUserAccounts_WithMissingAccount_ShouldFilterNull() {
        Account account1 = new Account(AccountType.DEBIT);
        String missingAccountId = UUID.randomUUID().toString();
        User user = new User("John Doe", "johndoe@example.com", "pass");
        user.setAccountIds(List.of(account1.getId(), missingAccountId));

        when(mockAccountRepository.getAccountById(account1.getId())).thenReturn(Optional.of(account1));
        when(mockAccountRepository.getAccountById(missingAccountId)).thenReturn(Optional.empty());
        List<Account> accounts = accountService.getUserAccounts(user);

        assertEquals(1, accounts.size());
        assertEquals(account1, accounts.get(0));
    }
}