package ru.yandex.practicum.filmorate.controller;

import ru.yandex.practicum.filmorate.model.User;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final List<User> users = new ArrayList<>();
    private int nextId = 1;

    @PostMapping
    public User createUser(@RequestBody User user) {
        user.setId(nextId++);
        users.add(user);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId() == user.getId()) {
                users.set(i, user);
                return user;
            }
        }
        throw new RuntimeException("User not found with id: " + user.getId());
    }

    @GetMapping
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }
}