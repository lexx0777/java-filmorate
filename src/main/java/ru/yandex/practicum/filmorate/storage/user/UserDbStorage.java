package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.sql.Date;
import java.util.Collection;
import java.util.Optional;

@Repository("UserDbStorage")
public class UserDbStorage extends BaseDbStorage<User> implements UserStorage {

    private static final String FIND_BY_ID = "SELECT u.*, " +
            "ARRAY_AGG(DISTINCT f.friend_id) FILTER (WHERE f.friend_id IS NOT NULL) AS friends " +
            "FROM users AS u " +
            "LEFT JOIN friends AS f ON u.id = f.user_id " +
            "WHERE u.id = ? " +
            "GROUP BY u.id;";

    private static final String FIND_ALL = "SELECT u.*, " +
            "ARRAY_AGG(DISTINCT f.friend_id) FILTER (WHERE f.friend_id IS NOT NULL) AS friends " +
            "FROM users AS u " +
            "LEFT JOIN friends AS f ON u.id = f.user_id " +
            "GROUP BY u.id;";

    private static final String INSERT = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";

    private static final String UPDATE_USER = "UPDATE users SET email = ?, login = ?, " +
            "name = ?, birthday = ? WHERE id = ?";

    private static final String INSERT_FRIEND = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?)";

    private static final String DELETE_FRIEND = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";

    private static final String DELETE_ALL = "DELETE FROM users";

    public UserDbStorage(JdbcTemplate jdbc, UserRowMapper mapper) {
        super(jdbc, mapper);
    }

    public Optional<User> getUser(Long id) {
        return findOne(FIND_BY_ID, id);
    }

    public Collection<User> getUsers() {
        return findMany(FIND_ALL);
    }

    public User addUser(User user) {
        long id = insert(
                INSERT,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday())
        );

        user.setId(id);
        return user;
    }

    public User updateUser(User user) {
        update(
                UPDATE_USER,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );

        return user;
    }

    public void addFriend(Long userId, Long friendId) {
        insertWithoutGeneratedKeys(INSERT_FRIEND, userId, friendId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        delete(DELETE_FRIEND, userId, friendId);
    }

    public void clearData() {
        delete(DELETE_ALL);
    }
}