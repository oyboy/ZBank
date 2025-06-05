package org.example.services;

import lombok.RequiredArgsConstructor;
import org.example.models.Account;
import org.example.models.Transaction;
import org.example.models.User;
import org.example.models.enums.TransactionType;
import org.example.repositories.AccountRepository;
import org.example.repositories.TransactionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
@RequiredArgsConstructor
public class BankService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public BankService() {
        transactionRepository = new TransactionRepository();
        accountRepository = new AccountRepository();
    }

    public void deposit(String accountId, double amount) throws Exception {
        Account account = accountRepository.getAccountById(accountId).orElse(null);
        if (account == null) throw new RuntimeException("Account not found");
        if (amount <= 0) throw new RuntimeException("Amount must be positive");

        account.setBalance(account.getBalance() + amount);
        Transaction transaction = Transaction.builder()
                .type(TransactionType.DEPOSIT)
                .amount(amount)
                .sourceAccountId(accountId)
                .timestamp(LocalDateTime.now())
                .build();

        accountRepository.save(account);
        transactionRepository.save(transaction);
    }

    public void withdraw(String accountId, double amount) throws Exception {
        Account account = accountRepository.getAccountById(accountId).orElse(null);
        if (account == null) throw new RuntimeException("Account not found");
        if (amount <= 0) throw new RuntimeException("Amount must be positive");
        if (account.getBalance() < amount) throw new RuntimeException("Not enough balance");

        account.setBalance(account.getBalance() - amount);
        Transaction transaction = Transaction.builder()
                .type(TransactionType.WITHDRAW)
                .amount(amount)
                .sourceAccountId(accountId)
                .timestamp(LocalDateTime.now())
                .build();

        accountRepository.save(account);
        transactionRepository.save(transaction);
    }
    public void transfer(String fromAccountId, String toAccountId, double amount) throws Exception {
        Account senderAccount = accountRepository.getAccountById(fromAccountId).orElse(null);
        Account receiverAccount = accountRepository.getAccountById(toAccountId).orElse(null);

        if (senderAccount == null) throw new RuntimeException("Sender account not found");
        if (receiverAccount == null) throw new RuntimeException("Receiver account not found");
        if (amount <= 0) throw new RuntimeException("Amount must be positive");
        if (senderAccount.getBalance() < amount) throw new RuntimeException("Not enough balance");

        senderAccount.setBalance(senderAccount.getBalance() - amount);
        receiverAccount.setBalance(receiverAccount.getBalance() + amount);
        Transaction transaction = Transaction.builder()
                .type(TransactionType.TRANSFER)
                .amount(amount)
                .sourceAccountId(fromAccountId)
                .targetAccountId(toAccountId)
                .timestamp(LocalDateTime.now())
                .build();

        accountRepository.save(senderAccount);
        accountRepository.save(receiverAccount);
        transactionRepository.save(transaction);
    }

    public List<Transaction> getTransactionHistoryByUser(User user) {
        List<String> accountIds = user.getAccountIds();
        return accountIds.stream()
                .flatMap(accountId -> transactionRepository.getAllTransactionsBySourceId(accountId).stream())
                .collect(Collectors.toList());
    }
}