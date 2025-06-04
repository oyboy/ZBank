package org.example.commands;

import org.example.models.Session;
import org.example.services.BankService;
import picocli.CommandLine;

@CommandLine.Command(name = "deposit", mixinStandardHelpOptions = true, description = "Deposit money into an account")
public class DepositCommand implements Runnable {
    @CommandLine.Option(names = {"-n", "--number"}, description = "Account id", required = true)
    private String number;

    @CommandLine.Option(names = {"-m", "--amount"}, description = "Amount to deposit", required = true)
    private double amount;

    private final BankService bankService;

    public DepositCommand() {
        this.bankService = new BankService();
    }

    @Override
    public void run() {
        if (!Session.isAuthenticated()) {
            System.out.println("You must login first.");
            return;
        }
        if (!Session.getCurrentUser().getAccountIds().contains(number)) {
            System.out.println("You can deposit only your accounts");
            return;
        }
        try {
            bankService.deposit(number, amount);
            System.out.println("Deposited " + amount + " into account " + number);
        } catch (Exception e) {
            System.out.println("Can't deposit " + amount + " into account " + number + ": " + e.getMessage());
        }
    }
}

