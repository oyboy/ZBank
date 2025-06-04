package org.example.repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.models.User;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
public class UserRepository {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final String USER_PATH;

    public UserRepository() {
        USER_PATH = "database/users.json";
    }

    public UserRepository(String USER_PATH) {
        this.USER_PATH = USER_PATH;
    }

    public void save(User user) {
        List<User> users = getAllUsers();
        users.stream()
                .filter(u -> u.getId().equals(user.getId()) || u.getEmail().equals(user.getEmail()))
                .findFirst()
                .ifPresent(users::remove);
        users.add(user);
        saveAll(users);
    }


    private void saveAll(List<User> users) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(USER_PATH), users);
        } catch (IOException io) {
            System.err.println("Error writing users list to file: " + io.getMessage());
        }
    }

    public List<User> getAllUsers() {
        try {
            File file = new File(USER_PATH);
            if (!file.exists()) return new ArrayList<>();
            return objectMapper.readValue(file, new TypeReference<>() {
            });
        } catch (IOException e) {
            System.err.println("Error receiving users from file: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Optional<User> getUserByEmail(String email) {
        return getAllUsers().stream().
                filter(user -> user.getEmail().equals(email)).
                findFirst();
    }
}
