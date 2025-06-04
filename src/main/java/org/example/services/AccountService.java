package org.example.services;

import org.example.models.Account;
import org.example.models.User;
import org.example.models.enums.AccountType;
import org.example.repositories.AccountRepository;
import org.example.repositories.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AccountService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountService(){
        accountRepository = new AccountRepository();
        userRepository = new UserRepository();
    }

    public void createAccount(User user, AccountType accountType) {
        Account account = new Account(accountType);
        accountRepository.save(account);

        user.getAccountIds().add(account.getId());
        userRepository.save(user);
    }

    public List<Account> getUserAccounts(User user) {
        return user.getAccountIds().stream()
                .map(accountId -> accountRepository.getAccountById(accountId).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    public String formatAccountDetails(Account account) {
        return String.format("│ %-36s │ %-15s │ %10.2f │",
                account.getId(),
                account.getAccountType(),
                account.getBalance());
    }
}
