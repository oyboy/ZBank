package org.example.services;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.example.models.Account;
import org.example.models.Transaction;
import org.example.models.User;
import org.example.models.enums.AccountType;
import org.example.models.enums.TransactionType;
import org.example.repositories.AccountRepository;
import org.example.repositories.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Epic("Transactions and Accounts")
@Feature("Transaction creation and search")
@DisplayName("Unit tests for BankService")
public class BankServiceTest {
    @Mock
    private TransactionRepository mockTransactionRepository;
    @Mock
    private AccountRepository mockAccountRepository;

    private BankService bankService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        bankService = new BankService(mockTransactionRepository, mockAccountRepository);
    }

    @Test
    @DisplayName("Deposit should increase balance and create transaction")
    public void testDeposit_ShouldIncreaseBalanceAndCreateTransaction() throws Exception {
        String accountId = "acc1";
        double initialBalance = 100.0;
        Account account = new Account(accountId, initialBalance, AccountType.DEBIT);
        double depositAmount = 50.0;

        when(mockAccountRepository.getAccountById(accountId)).thenReturn(Optional.of(account));
        bankService.deposit(accountId, depositAmount);

        assertEquals(initialBalance + depositAmount, account.getBalance());
        verify(mockAccountRepository, times(1)).save(account);
        verify(mockTransactionRepository, times(1)).save(argThat(transaction ->
                transaction.getType() == TransactionType.DEPOSIT &&
                        transaction.getAmount() == depositAmount &&
                        transaction.getSourceAccountId().equals(accountId)
        ));
    }

    @Test
    @DisplayName("Deposit with non-existent account should throw exception")
    public void testDeposit_WithNonExistentAccount_ShouldThrowException() {
        String accountId = "non-existent";

        when(mockAccountRepository.getAccountById(accountId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> bankService.deposit(accountId, 50.0));
    }

    @Test
    @DisplayName("Deposit with negative amount should throw exception")
    public void testDeposit_WithNegativeAmount_ShouldThrowException() {
        String accountId = "acc1";
        Account account = new Account(accountId, 100.0, AccountType.DEBIT);

        when(mockAccountRepository.getAccountById(accountId)).thenReturn(Optional.of(account));

        assertThrows(RuntimeException.class, () -> bankService.deposit(accountId, -50.0));
    }

    @Test
    @DisplayName("Withdrawal should decrease balance and create transaction")
    public void testWithdraw_ShouldDecreaseBalanceAndCreateTransaction() throws Exception {
        String accountId = "acc1";
        double initialBalance = 100.0;
        double withdrawAmount = 50.0;
        Account account = new Account(accountId, initialBalance, AccountType.DEBIT);

        when(mockAccountRepository.getAccountById(accountId)).thenReturn(Optional.of(account));
        bankService.withdraw(accountId, withdrawAmount);

        assertEquals(initialBalance - withdrawAmount, account.getBalance());
        verify(mockAccountRepository, times(1)).save(account);
        verify(mockTransactionRepository, times(1)).save(argThat(transaction ->
                transaction.getType() == TransactionType.WITHDRAW &&
                        transaction.getAmount() == withdrawAmount &&
                        transaction.getSourceAccountId().equals(accountId)
        ));
    }

    @Test
    @DisplayName("Withdrawal with insufficient balance should throw exception")
    public void testWithdraw_WithInsufficientBalance_ShouldThrowException() {
        String accountId = "acc1";
        Account account = new Account(accountId, 30.0, AccountType.DEBIT);

        when(mockAccountRepository.getAccountById(accountId)).thenReturn(Optional.of(account));

        assertThrows(RuntimeException.class, () -> bankService.withdraw(accountId, 50.0));
    }

    @Test
    @DisplayName("Transfer should update both account balances and create transaction")
    public void testTransfer_ShouldUpdateBalancesAndCreateTransaction() throws Exception {
        String fromAccountId = "acc1";
        String toAccountId = "acc2";
        double initialBalance1 = 100.0;
        double initialBalance2 = 50.0;
        double transferAmount = 30.0;

        Account fromAccount = new Account(fromAccountId, initialBalance1, AccountType.DEBIT);
        Account toAccount = new Account(toAccountId, initialBalance2, AccountType.DEBIT);

        when(mockAccountRepository.getAccountById(fromAccountId)).thenReturn(Optional.of(fromAccount));
        when(mockAccountRepository.getAccountById(toAccountId)).thenReturn(Optional.of(toAccount));
        bankService.transfer(fromAccountId, toAccountId, transferAmount);

        assertEquals(initialBalance1 - transferAmount, fromAccount.getBalance());
        assertEquals(initialBalance2 + transferAmount, toAccount.getBalance());
        verify(mockAccountRepository, times(1)).save(fromAccount);
        verify(mockAccountRepository, times(1)).save(toAccount);
        verify(mockTransactionRepository, times(1)).save(argThat(transaction ->
                transaction.getType() == TransactionType.TRANSFER &&
                        transaction.getAmount() == transferAmount &&
                        transaction.getSourceAccountId().equals(fromAccountId) &&
                        transaction.getTargetAccountId().equals(toAccountId)
        ));
    }

    @Test
    @DisplayName("Getting transaction history should return transactions for all user's accounts")
    public void testGetTransactionHistoryByUser_ShouldReturnAllUserTransactions() {
        String accountId1 = "acc1";
        String accountId2 = "acc2";
        User user = new User();
        user.setAccountIds(List.of(accountId1, accountId2));

        Transaction transaction1 = Transaction.builder()
                .sourceAccountId(accountId1)
                .type(TransactionType.DEPOSIT)
                .amount(100.0)
                .build();

        Transaction transaction2 = Transaction.builder()
                .sourceAccountId(accountId2)
                .type(TransactionType.WITHDRAW)
                .amount(50.0)
                .build();

        when(mockTransactionRepository.getAllTransactionsBySourceId(accountId1))
                .thenReturn(List.of(transaction1));
        when(mockTransactionRepository.getAllTransactionsBySourceId(accountId2))
                .thenReturn(List.of(transaction2));
        List<Transaction> transactions = bankService.getTransactionHistoryByUser(user);

        assertEquals(2, transactions.size());
        assertTrue(transactions.contains(transaction1));
        assertTrue(transactions.contains(transaction2));
    }
}
