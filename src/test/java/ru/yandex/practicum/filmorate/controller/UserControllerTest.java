package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController();
    }

    @Test
    void findAllShouldReturnEmptyCollectionInitially() {
        Collection<User> users = userController.findAll();
        assertTrue(users.isEmpty(), "Should return empty collection initially");
    }

    @Test
    void createUserShouldAssignIdAndAddToStorage() {
        User user = createValidUser();
        User createdUser = userController.createUser(user);

        assertNotNull(createdUser.getId(), "Should assign ID to created user");
        assertEquals(1, userController.findAll().size(), "Should add user to storage");
    }

    @Test
    void createUserShouldUseLoginWhenNameIsEmpty() {
        User user = createValidUser();
        user.setName("");
        User createdUser = userController.createUser(user);

        assertEquals(user.getLogin(), createdUser.getName(), "Should use login when name is empty");
    }

    @Test
    void createUserShouldRejectInvalidEmail() {
        User user = createValidUser();
        user.setEmail("invalid-email");

        assertThrows(ResponseStatusException.class, () -> userController.createUser(user),
                "Should reject user with invalid email");
    }

    @Test
    void updateUserShouldModifyExistingUser() {
        User original = userController.createUser(createValidUser());
        User updated = createValidUser();
        updated.setId(original.getId());
        updated.setName("Updated Name");

        User result = userController.updateUser(updated);
        assertEquals("Updated Name", result.getName(), "Should update user name");
    }

    @Test
    void updateUserShouldThrowWhenIdNotFound() {
        User user = createValidUser();
        user.setId(999);

        assertThrows(NotFoundException.class, () -> userController.updateUser(user),
                "Should throw when updating non-existent user");
    }

    @Test
    void findUserByIdShouldReturnExistingUser() {
        User user = userController.createUser(createValidUser());
        User found = userController.findUserById(user.getId());

        assertEquals(user.getId(), found.getId(), "Should return user by ID");
    }

    @Test
    void findUserByIdShouldThrowWhenNotFound() {
        assertThrows(NotFoundException.class, () -> userController.findUserById(999),
                "Should throw when user not found");
    }

    @Test
    void deleteUserByIdShouldRemoveUser() {
        User user = userController.createUser(createValidUser());
        userController.deleteUserById(user.getId());

        assertThrows(NotFoundException.class, () -> userController.findUserById(user.getId()),
                "Should remove user from storage");
    }

    /*
    @ParameterizedTest
    @MethodSource("invalidUsersProvider")
    void shouldRejectInvalidUsers(User invalidUser) {
        assertThrows(ResponseStatusException.class, () -> userController.createUser(invalidUser),
                "Should reject invalid user");
    }*/

    static Stream<User> invalidUsersProvider() {
        return Stream.of(
                createUser("", "login", LocalDate.of(1990, 1, 1)), // Empty email
                createUser("invalid", "login", LocalDate.of(1990, 1, 1)), // Invalid email
                createUser("valid@email.com", "", LocalDate.of(1990, 1, 1)), // Empty login
                createUser("valid@email.com", "with space", LocalDate.of(1990, 1, 1)), // Login with space
                createUser("valid@email.com", "login", LocalDate.now().plusDays(1)) // Future birthday
        );
    }

    private static User createValidUser() {
        return createUser("email@test.com", "login", LocalDate.of(1990, 1, 1));
    }

    private static User createUser(String email, String login, LocalDate birthday) {
        User user = new User();
        user.setEmail(email);
        user.setLogin(login);
        user.setBirthday(birthday);
        return user;
    }
}