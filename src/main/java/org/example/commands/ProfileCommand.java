package org.example.commands;

import org.example.models.Account;
import org.example.models.Session;
import org.example.models.User;
import org.example.services.AccountService;
import picocli.CommandLine;

import java.util.List;

@CommandLine.Command(name = "profile", mixinStandardHelpOptions = true, description = "Show user profile")
public class ProfileCommand implements Runnable {
    private final AccountService accountService;

    public ProfileCommand(){
        this.accountService = new AccountService();
    }

    @Override
    public void run() {
        if (!Session.isAuthenticated()) {
            System.out.println("You must login first.");
            return;
        }

        User user = Session.getCurrentUser();
        List<Account> accounts = accountService.getUserAccounts(user);

        System.out.println();
        System.out.println("┌─────────────────────────────────────────────────────────────────┐");
        System.out.println("│                       USER PROFILE                              │");
        System.out.println("├─────────────────────────────────────────────────────────────────┤");
        System.out.printf("│ %-20s: %-40s │\n", "Name", user.getName());
        System.out.printf("│ %-20s: %-40s │\n", "Email", user.getEmail());
        System.out.printf("│ %-20s: %-40s │\n", "User ID", user.getId());
        System.out.println("├─────────────────────────────────────────────────────────────────┤");
        System.out.println("│                            ACCOUNTS                             │");
        System.out.println("├──────────────────────────────────────┬────────────────┬─────────┤");
        System.out.println("│ ID                                   │ Type           │ Balance │");
        System.out.println("├──────────────────────────────────────┼────────────────┼─────────┤");

        if (accounts.isEmpty()) {
            System.out.println("│ No accounts found                                      │");
        } else {
            accounts.forEach(account ->
                    System.out.println(accountService.formatAccountDetails(account)));
        }
        System.out.println("└──────────────────────────────────────┴────────────────┴─────────┘");
        System.out.println();
    }
}