package org.example.commands;

import org.example.models.Session;
import org.example.models.Transaction;
import org.example.services.BankService;
import picocli.CommandLine;

import java.util.List;

@CommandLine.Command(name = "history", mixinStandardHelpOptions = true, description = "View transaction history of an account")
public class HistoryCommand implements Runnable {
    private final BankService bankService;

    public HistoryCommand() {
        this.bankService = new BankService();
    }

    @Override
    public void run() {
        if (!Session.isAuthenticated()) {
            System.out.println("You must login first.");
            return;
        }
        try {
            List<Transaction> transactions = bankService.getTransactionHistoryByUser(Session.getCurrentUser());
            if (transactions.isEmpty()) {
                System.out.println("No transactions found for your account ");
            } else {
                transactions.forEach(transaction -> {
                    System.out.println(transaction.getTimestamp() + " | " +
                            transaction.getType() + " | Amount: " + transaction.getAmount());
                });
            }
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}

