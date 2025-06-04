package org.example.commands;

import org.example.models.Session;
import org.example.models.User;
import org.example.services.RegistrationService;
import picocli.CommandLine;

import java.util.Optional;

@CommandLine.Command(name="login", description = "Авторизация пользователя")
public class LoginUserCommand implements Runnable {
    @CommandLine.Option(names = {"-e", "--email"}, required = true)
    String email;

    @CommandLine.Option(names = {"-p", "--password"}, required = true)
    String password;

    @Override
    public void run() {
        RegistrationService service = new RegistrationService();
        Optional<User> user = service.login(email, password);
        if (user.isPresent()) {
            Session.setCurrentUser(user.get());
            System.out.println("Login successful!");
        } else {
            System.out.println("Invalid credentials.");
        }
    }
}