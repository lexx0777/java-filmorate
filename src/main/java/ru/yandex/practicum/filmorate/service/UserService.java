package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.OtherException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public Collection<User> getAllUsers() {
        return userStorage.getUsers();
    }

    public User getUserById(Long id) {
        return userStorage.getUser(id).orElseThrow(() -> new NotFoundException("Юзер с id " + id + " не найден"));
    }

    public User createUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("В запросе создания пользователя передан пустой name. Для поля name использован логин {}",
                    user.getLogin());
        }

        return userStorage.addUser(user);
    }

    public User updateUser(User newUser) {
        if (newUser.getId() == null) {
            log.warn("В запросе на обновление юзера не передан id");
            throw new ValidationException("Id не может быть пустым");
        }

        userStorage.getUser(newUser.getId())
                .orElseThrow(() -> {
                    log.warn("В запросе на обновление пользователя передан неизвестный id - {}", newUser.getId());
                    return new NotFoundException("Пользователь с id " + newUser.getId() + " не найден");
                });

        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
            log.info("В запросе обновления пользователя передан пустой name. Для поля name использован логин {}",
                    newUser.getLogin());
        }

        return userStorage.updateUser(newUser);
    }

    public void addFriend(Long id, Long friendId) {
        User user = getUserById(id);
        User friend = getUserById(friendId);

        if (id.equals(friendId)) {
            log.warn("Пользователь не может добавить сам себя в друзья");
            throw new OtherException("Пользователь не может добавить сам себя в друзья");
        }

        if (!user.getFriends().add(friendId) || !friend.getFriends().add(id)) {
            log.warn("Пользователи с id {} и id {} уже являются друзьями", id, friendId);
            throw new OtherException("Пользователи уже являются друзьями");
        }

        // Добавляем друзей в БД
        userStorage.addFriend(id, friendId);
        userStorage.addFriend(friendId, id); // взаимная дружба

        log.info("Пользователь с id {} добавил в друзья пользователя с id {}", id, friendId);
    }

    public void removeFriend(Long id, Long friendId) {
        User user = getUserById(id);
        User friend = getUserById(friendId);

        if (!user.getFriends().remove(friendId) || !friend.getFriends().remove(id)) {
            log.warn("У пользователя с id {} не найден друг с id {}", id, friendId);
        }

        // Удаляем друзей из БД
        userStorage.deleteFriend(id, friendId);
        userStorage.deleteFriend(friendId, id);

        log.info("Пользователь с id {} удалил из друзей пользователя с id {}", id, friendId);
    }

    public Collection<User> getUserFriends(Long id) {
        return getUserById(id).getFriends().stream()
                .map(this::getUserById).toList();
    }

    public Collection<User> getCommonUserFriends(Long id, Long otherId) {
        return getUserById(id).getFriends().stream()
                .filter(getUserById(otherId).getFriends()::contains)
                .map(this::getUserById).toList();
    }

    public void clearUsersData() {
        userStorage.clearData();
    }
}