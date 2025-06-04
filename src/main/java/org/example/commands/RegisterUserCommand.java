package org.example.commands;

import org.example.services.RegistrationService;
import picocli.CommandLine;

@CommandLine.Command(name="reg", description = "Регистрация пользователя")
public class RegisterUserCommand implements Runnable {
    @CommandLine.Option(names = {"-u", "--username"}, required = true)
    String username;

    @CommandLine.Option(names = {"-e", "--email"}, required = true)
    String email;

    @CommandLine.Option(names = {"-p", "--password"}, required = true)
    String password;

    @CommandLine.Option(names = {"-cp", "--confirmPassword"}, required = true)
    String confirmPassword;

    private final RegistrationService registrationService;

    public RegisterUserCommand(){
        registrationService = new RegistrationService();
    }

    @Override
    public void run() {
        try{
            registrationService.register(username, email, password, confirmPassword);
        } catch (Exception e) {
            System.err.println("Cannot register user: " + e.getMessage());
        }
    }
}
