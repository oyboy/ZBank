package org.example.services;

import lombok.RequiredArgsConstructor;
import org.example.models.User;
import org.example.repositories.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class RegistrationService {
    private final UserRepository userRepository;

    public RegistrationService() {
        userRepository = new UserRepository();
    }

    public void register(String name, String email, String password, String confirmPassword)
            throws RuntimeException {
        if (name == null || email == null || password == null || confirmPassword == null)
            throw new NullPointerException("name, email and password cannot be null");
        if (!password.equals(confirmPassword))
            throw new RuntimeException("Passwords do not match");
        if (userRepository.getUserByEmail(email).isPresent())
            throw new RuntimeException("User already exists");

        String emailRegex = "^\\S+@\\S+\\.\\S+$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            throw new RuntimeException("Invalid email format");
        }

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
