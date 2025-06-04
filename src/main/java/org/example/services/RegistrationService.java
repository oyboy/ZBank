package org.example.services;

import org.example.models.User;
import org.example.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

public class RegistrationService {
    private final UserRepository userRepository;

    public RegistrationService() {
        userRepository = new UserRepository();
    }

    public void register(String name, String email, String password, String confirmPassword) throws Exception {
        if (name == null || email == null || password == null || confirmPassword == null)
            throw new NullPointerException("name, email and password cannot be null");
        if (!password.equals(confirmPassword))
            throw new RuntimeException("Passwords do not match");
        if (userRepository.getUserByEmail(email).isPresent())
            throw new RuntimeException("Account already exists");

        User user = new User(name, email, password);
        userRepository.save(user);
    }

    public Optional<User> login(String email, String password) {
        List<User> users = userRepository.getAllUsers();
        return users.stream()
                .filter(acc -> acc.getEmail().equals(email))
                .filter(acc -> acc.getPassword().equals(password))
                .findFirst();
    }
}
