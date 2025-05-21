package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
@Validated
@Slf4j
public class UserController {
    private final List<User> users = new ArrayList<>();
    private int nextId = 1;

    @GetMapping
    public Collection<UserApiDto> getUsers() {
        return users.getUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return users.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public User deleteUserById(@PathVariable Long id) {
        return users.deleteUserById(id);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Создание нового пользователя: {}", user);
        try {
            user.setId(nextId++);
            users.add(user);
            log.info("Пользователь успешно создан: {}", user);
            return user;
        } catch (Exception e) {
            log.error("Ошибка при создании пользователя: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        // логика обновления
    }
}