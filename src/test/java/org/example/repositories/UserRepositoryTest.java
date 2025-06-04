package org.example.repositories;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.models.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UserRepositoryTest {
    private final String TEST_USER_PATH = "database/test_users.json";
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() throws IOException {
        userRepository = new UserRepository(TEST_USER_PATH);
        File file = new File(TEST_USER_PATH);
        boolean created = file.createNewFile();
        if (created) new ObjectMapper().writeValue(file, new ArrayList<>());
    }
    @AfterEach
    public void tearDown() {
        File file = new File(TEST_USER_PATH);
        if (file.exists()) file.delete();
    }

    @Test
    @DisplayName("Сохранение и получение пользователя")
    public void testSaveAndGetUser() {
        User user = new User("John Doe", "john@example.com", "pass123");
        userRepository.save(user);

        List<User> allUsers = userRepository.getAllUsers();
        assertEquals(1, allUsers.size());
        assertEquals("John Doe", allUsers.get(0).getName());

        Optional<User> userByEmail = userRepository.getUserByEmail("john@example.com");
        assertTrue(userByEmail.isPresent());
        assertEquals(user.getId(), userByEmail.get().getId());
    }

    @Test
    @DisplayName("Обновление существующего пользователя")
    public void testUpdateUser() {
        User user = new User("John Doe", "john@example.com", "pass123");
        userRepository.save(user);

        User updatedUser = new User("John Smith", "john@example.com", "newpass456");
        userRepository.save(updatedUser);

        List<User> allUsers = userRepository.getAllUsers();
        assertEquals(1, allUsers.size());
        assertEquals("John Smith", allUsers.get(0).getName());
        assertEquals("newpass456", allUsers.get(0).getPassword());
    }

    @Test
    @DisplayName("Получение пользователя по сущестующему email")
    public void testGetUserByEmail() {
        User user = new User("John Doe", "john@example.com", "pass123");
        userRepository.save(user);

        Optional<User> userByEmail = userRepository.getUserByEmail("john@example.com");
        assertTrue(userByEmail.isPresent());
    }

    @Test
    @DisplayName("Получение пользователя по несуществующему email")
    public void testGetUserByInvalidEmail() {
        Optional<User> user = userRepository.getUserByEmail("nonexistent@example.com");
        assertTrue(user.isEmpty());
    }
}
