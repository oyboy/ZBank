package org.example.services;

import org.example.models.User;
import org.example.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Unit test for registration service")
public class RegistrationServiceTest {
    @Mock
    private UserRepository mockUserRepository;
    private RegistrationService registrationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        registrationService = new RegistrationService(mockUserRepository);
    }

    @Test
    @DisplayName("Проверка регистрации пользователя с валидными данными")
    public void testRegister_WhenProvidedValidData_ShouldSaveUser() {
        String name = "John Doe";
        String email = "johndoe@example.com";
        String password = "password123";
        String confirmPassword = "password123";

        when(mockUserRepository.getUserByEmail(email)).thenReturn(Optional.empty());
        registrationService.register(name, email, password, confirmPassword);

        verify(mockUserRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Проверка регистрации с несовпадающим паролем")
    public void testRegister_WhenProvidedInvalidData_ShouldThrowException() {
        String name = "John Doe";
        String email = "johndoe@example.com";
        String password = "password123";
        String confirmPassword = "password";

        when(mockUserRepository.getUserByEmail(email)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> registrationService.register(name, email, password, confirmPassword)
        );
        assertEquals("Passwords do not match", exception.getMessage());
    }

    @Test
    @DisplayName("Попытка зарегистрировать существующего пользователя")
    public void testRegister_WhenUserAlreadyExists_ShouldThrowException() {
        String name = "John Doe";
        String email = "johndoe@example.com";
        String password = "password123";
        String confirmPassword = "password123";

        when(mockUserRepository.getUserByEmail(email)).thenReturn(Optional.of(new User()));
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> registrationService.register(name, email, password, confirmPassword)
        );

        assertEquals("User already exists", exception.getMessage());
    }

    @Test
    @DisplayName("Регистрация с null-полями")
    public void testRegister_WhenSomeFieldIsNull_ShouldThrowException() {
        String name = "John Doe";
        String email = null;
        String password = "password123";
        String confirmPassword = "password123";

        when(mockUserRepository.getUserByEmail(email)).thenReturn(Optional.empty());
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> registrationService.register(name, email, password, confirmPassword)
        );

        assertEquals("name, email and password cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Регистрация с невалидным email")
    public void testRegister_WhenProvidedInvalidEmail_ShouldThrowException() {
        String name = "John Doe";
        String email = "johndoe@.com";
        String password = "password123";
        String confirmPassword = "password123";

        when(mockUserRepository.getUserByEmail(email)).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> registrationService.register(name, email, password, confirmPassword)
        );

        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    @DisplayName("Авторизация с валидными данными")
    public void testLogin_WhenProvidedValidData_ShouldReturnUser() {
        String email = "johndoe@example.com";
        String password = "password123";
        User user = new User("John Doe", email, password);

        when(mockUserRepository.getAllUsers()).thenReturn(List.of(user));
        Optional<User> result = registrationService.login(email, password);

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    @DisplayName("Авторизация с невалидным паролем")
    public void testLogin_WhenProvidedInvalidPassword_ShouldReturnEmptyUser(){
        String email = "johndoe@example.com";
        String password = "wrongpassword";
        User user = new User("John Doe", email, "password123");

        when(mockUserRepository.getAllUsers()).thenReturn(List.of(user));
        Optional<User> result = registrationService.login(email, password);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Авторизация по несуществующему email")
    public void testLogin_WhenProvidedInvalidEmail_ShouldReturnEmptyUser(){
        String email = "nonexistent@example.com";
        String password = "password123";

        when(mockUserRepository.getAllUsers()).thenReturn(List.of());
        Optional<User> result = registrationService.login(email, password);

        assertFalse(result.isPresent());
    }
}