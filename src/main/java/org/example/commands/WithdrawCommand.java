package org.example.commands;

import org.example.models.Session;
import org.example.services.BankService;
import picocli.CommandLine;

@CommandLine.Command(name = "withdraw", mixinStandardHelpOptions = true, description = "Withdraw money from an account")
public class WithdrawCommand implements Runnable {
    @CommandLine.Option(names = {"-n", "--number"}, description = "Account id", required = true)
    private String number;

    @CommandLine.Option(names = {"-m", "--amount"}, description = "Amount to withdraw", required = true)
    private double amount;

    private final BankService bankService;

    public WithdrawCommand() {
        this.bankService = new BankService();
    }

    @Override
    public void run() {
        if (!Session.isAuthenticated()) {
            System.out.println("You must login first.");
            return;
        }
        if (!Session.getCurrentUser().getAccountIds().contains(number)){
            System.out.println("You can withdraw only your account");
            return;
        }
        try {
            bankService.withdraw(number, amount);
            System.out.println("Withdrew " + amount + " from account " + number);
        } catch (Exception e) {
            System.out.println("Can't withdraw " + amount + " from account " + number + ": " + e.getMessage());
        }
    }
}
