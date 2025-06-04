package org.example.commands;

import org.example.models.Session;
import org.example.services.BankService;
import picocli.CommandLine;

@CommandLine.Command(name = "transfer", mixinStandardHelpOptions = true, description = "Transfer money between accounts")
public class TransferCommand implements Runnable {
    @CommandLine.Option(names = {"-f", "--fromAccountId"}, description = "Source account ID", required = true)
    private String fromAccountId;

    @CommandLine.Option(names = {"-t", "--toAccountId"}, description = "Target account ID", required = true)
    private String toAccountId;

    @CommandLine.Option(names = {"-m", "--amount"}, description = "Amount to transfer", required = true)
    private double amount;

    private final BankService bankService;

    public TransferCommand() {
        this.bankService = new BankService();
    }

    @Override
    public void run() {
        if (!Session.isAuthenticated()) {
            System.out.println("You must login first.");
            return;
        }
        if (!Session.getCurrentUser().getAccountIds().contains(fromAccountId)) {
            System.out.println("You can transfer money only from your account");
            return;
        }
        try {
            bankService.transfer(fromAccountId, toAccountId, amount);
            System.out.println("Transferred " + amount + " from account " + fromAccountId + " to account " + toAccountId);
        } catch (Exception e) {
            System.out.println("Can't transfer money between accounts " + fromAccountId + " to " + toAccountId + " due to " + e.getMessage());
        }
    }
}
