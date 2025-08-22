package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private final UserService userService;

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @GetMapping("/{id}")
    public User findUserById(@PathVariable int id) {
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
    @ResponseStatus(HttpStatus.CREATED)
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
            //user.validate();
            user.setId((long) getNextId());
            users.put(user.getId().intValue(), user);
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
            if (users.containsKey(newUser.getId().intValue())) {
                User oldUser = users.get(newUser.getId().intValue());
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

    @PutMapping("/{id}/friends/{friendId}")
    public Map<String, String> addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.addFriend(id, friendId);
        return Map.of("status", "success");
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public Map<String, String> removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.removeFriend(id, friendId);
        return Map.of("status", "success");
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getUserFriends(@PathVariable Long id) {
        return userService.getUserFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return userService.getCommonUserFriends(id, otherId);
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