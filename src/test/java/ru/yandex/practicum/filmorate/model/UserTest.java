package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    private final Validator validator;

    public UserTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void createValidUser() {
        User user = new User();
        user.setEmail("valid@email.com");
        user.setLogin("validLogin");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "No violations should be present for valid user");
    }

    @Test
    void validateEmailNotBlank() {
        User user = new User();
        user.setEmail("");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Violations should be present for blank email");
        assertEquals(1, violations.size());
        assertEquals("Электронная почта не может быть пустой", violations.iterator().next().getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid", "invalid@", "invalid.com"})
    void validateEmailFormat(String email) {
        User user = new User();
        user.setEmail(email);
        user.setLogin("login");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Violations should be present for invalid email format");
        assertEquals(1, violations.size());
        assertEquals("Электронная почта должна содержать символ @", violations.iterator().next().getMessage());
    }

    @Test
    void validateLoginNotBlank() {
        User user = new User();
        user.setEmail("email@test.com");
        user.setLogin("");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Violations should be present for blank login");
        assertEquals(2, violations.size(), "Should have 2 violations for blank login (NotBlank and Pattern)");

        // Verify both error messages are present
        Set<String> messages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
        assertTrue(messages.contains("Логин не может быть пустым"));
        assertTrue(messages.contains("Логин не может содержать пробелы"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"with space", "with\t tab", "with\n newline"})
    void validateLoginNoSpaces(String login) {
        User user = new User();
        user.setEmail("email@test.com");
        user.setLogin(login);
        user.setBirthday(LocalDate.of(1990, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Violations should be present for login with spaces");
        assertEquals(1, violations.size());
        assertEquals("Логин не может содержать пробелы", violations.iterator().next().getMessage());
    }

    @Test
    void validateBirthdayNotInFuture() {
        User user = new User();
        user.setEmail("email@test.com");
        user.setLogin("login");
        user.setBirthday(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Violations should be present for future birthday");
        assertEquals(1, violations.size());
        assertEquals("Дата рождения не может быть в будущем", violations.iterator().next().getMessage());
    }

    @Test
    void validateBirthdayBoundaryToday() {
        User user = new User();
        user.setEmail("email@test.com");
        user.setLogin("login");
        user.setBirthday(LocalDate.now());

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "No violations should be present for today's birthday");
    }

    @Test
    void getNameWhenNameIsNull() {
        User user = new User();
        user.setLogin("testLogin");
        user.setName(null);

        assertEquals("testLogin", user.getName(), "Should return login when name is null");
    }

    @Test
    void getNameWhenNameIsBlank() {
        User user = new User();
        user.setLogin("testLogin");
        user.setName("");

        assertEquals("testLogin", user.getName(), "Should return login when name is blank");
    }

    @Test
    void getNameWhenNameIsPresent() {
        User user = new User();
        user.setLogin("testLogin");
        user.setName("Test Name");

        assertEquals("Test Name", user.getName(), "Should return name when name is present");
    }
}