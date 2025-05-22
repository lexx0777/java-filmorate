package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.*;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@Validated
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @GetMapping("/{id}")
    public User findUserById(@PathVariable @Positive(message = "ID пользователя должен быть положительным числом") int id) {
        User user = users.get(id);
        if (user == null) {
            log.info("Пользователь с id " + id + " не найден");
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        return user;
    }

    @DeleteMapping("/{id}")
    public User deleteUserById(@PathVariable int id) {
        User user = users.get(id);
        if (user == null) {
            log.info("Пользователь с id " + id + " не найден");
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        return users.remove(id);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Создание нового пользователя: {}", user);
        try {
            Validator validator;
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            validator = factory.getValidator();

            // Validate the user
            Set<ConstraintViolation<User>> violations = validator.validate(user);
            if (!violations.isEmpty()) {
                String errorMessage = violations.stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.joining(", "));
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
            }
            user.validate();
            user.setId(getNextId());
            users.put(user.getId(), user);
            log.info("Пользователь успешно создан: {}", user);
            return user;
        } catch (Exception e) {
            log.error("Ошибка при создании пользователя: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User newUser) {
        log.info("Обновление пользователя: {}", newUser);
        try {
            if (users.containsKey(newUser.getId())) {
                User oldUser = users.get(newUser.getId());
                // если user найден и все условия соблюдены, обновляем её содержимое
                //todo проверить email и login на уникальность - потом
                oldUser.setEmail(newUser.getEmail());
                oldUser.setLogin(newUser.getLogin());
                oldUser.setBirthday(newUser.getBirthday());
                oldUser.setName(newUser.getName());
                return oldUser;
            }
            throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
        } catch (Exception e) {
            log.error("Ошибка при обновлении пользователя: {}", e.getMessage(), e);
            throw e;
        }
    }

    // вспомогательный метод для генерации нового id
    private Integer getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return  ++currentMaxId;
    }
}