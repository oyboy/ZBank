package org.example.commands;

import org.example.models.Session;
import org.example.models.enums.AccountType;
import org.example.services.AccountService;
import picocli.CommandLine;

@CommandLine.Command(name = "acc-create", mixinStandardHelpOptions = true, description = "Create account for current user")
public class AccountCreateCommand implements Runnable {
    @CommandLine.Option(names = {"-t", "--type"}, description = "Type of account: SAVING, CREDIT or DEBIT", required = true)
    private AccountType type;

    private final AccountService accountService;

    public AccountCreateCommand() {
        this.accountService = new AccountService();
    }

    @Override
    public void run() {
        if (!Session.isAuthenticated()) {
            System.out.println("You must login first.");
            return;
        }
        try {
            accountService.createAccount(Session.getCurrentUser(), type);
            System.out.println("Account created");
        } catch (Exception e) {
            System.out.println("Can't create account: " + e.getMessage());
        }
    }
}
