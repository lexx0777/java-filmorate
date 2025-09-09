package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.MpaRowMapper;

import java.util.List;
import java.util.Optional;

@Repository("MpaDbStorage")
public class MpaDbStorage extends BaseDbStorage<Mpa> implements MpaStorage {

    private static final String FIND_BY_ID = "SELECT * FROM mpa WHERE id = ?";
    private static final String FIND_ALL = "SELECT * FROM mpa";

    public MpaDbStorage(JdbcTemplate jdbc, MpaRowMapper mapper) {
        super(jdbc, mapper);
    }

    public Optional<Mpa> getMpa(Long id) {
        return findOne(FIND_BY_ID, id);
    }

    public List<Mpa> getAllRatings() {
        return findMany(FIND_ALL);
    }
}