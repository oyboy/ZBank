package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.commands.*;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

@CommandLine.Command(name = "zbank", description = "ZBank", subcommands = {
        RegisterUserCommand.class,
        LoginUserCommand.class,
        HistoryCommand.class,
        ProfileCommand.class,
        AccountCreateCommand.class,
        DepositCommand.class,
        WithdrawCommand.class,
        TransferCommand.class,
})
public class Main implements Runnable {
    @Override
    public void run() {
        new CommandLine(this).usage(System.out);
    }

    public static void main(String[] args) {
        init();
        Scanner scanner = new Scanner(System.in);
        System.out.println("For exit type 'exit' without quotes");

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Closing program");
                break;
            }

            String[] commandArgs = input.split(" ");
            new CommandLine(new Main()).execute(commandArgs);
        }
        scanner.close();
    }
    private static void init(){
        System.out.println("Initializing ZBank");
        try {
            File dir = new File("./database");
            if (!dir.exists()) dir.mkdir();
            File userFile = new File(dir, "users.json");
            boolean userCreated = userFile.createNewFile();

            File accountFile = new File(dir, "accounts.json");
            boolean accountCreated = accountFile.createNewFile();

            File transactionFile = new File(dir, "transactions.json");
            boolean transactionCreated = transactionFile.createNewFile();

            ObjectMapper mapper = new ObjectMapper();
            if (userCreated) {
                mapper.writeValue(userFile, new ArrayList<>());
            }
            if (accountCreated) {
                mapper.writeValue(accountFile, new ArrayList<>());
            }
            if (transactionCreated) {
                mapper.writeValue(transactionFile, new ArrayList<>());
            }
            System.out.println("Init completed");

        } catch (IOException io) {
            System.err.println("Can't create files: " + io.getMessage());
        }
    }
}