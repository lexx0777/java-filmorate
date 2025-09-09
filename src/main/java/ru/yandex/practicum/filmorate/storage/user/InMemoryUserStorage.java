package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    public Collection<User> getUsers() {
        return users.values();
    }

    public Optional<User> getUser(Long id) {
        return users.values().stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    public User addUser(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Добавлен пользователь: {}", user);
        return user;
    }

    public User updateUser(User newUser) {
        User oldUser = users.get(newUser.getId());
        Optional.of(newUser.getName()).ifPresent(oldUser::setName);
        Optional.ofNullable(newUser.getEmail()).ifPresent(oldUser::setEmail);
        Optional.ofNullable(newUser.getBirthday()).ifPresent(oldUser::setBirthday);
        Optional.ofNullable(newUser.getLogin()).ifPresent(oldUser::setLogin);
        log.info("Обновлен юзер: {}", newUser);
        return oldUser;
    }

    public void addFriend(Long userId, Long friendId) {
        User user = users.get(userId);
        user.getFriends().add(friendId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        User user = users.get(userId);
        user.getFriends().remove(friendId);
    }

    public void clearData() {
        users.clear();
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}