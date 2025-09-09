package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;

import java.util.List;
import java.util.Optional;

@Repository("GenreDbStorage")
public class GenreDbStorage extends BaseDbStorage<FilmGenre> implements GenreStorage {

    private static final String FIND_BY_ID = "SELECT * FROM genres WHERE id = ?;";
    private static final String FIND_ALL = "SELECT * FROM genres;";

    public GenreDbStorage(JdbcTemplate jdbc, GenreRowMapper mapper) {
        super(jdbc, mapper);
    }

    public Optional<FilmGenre> getGenre(Long id) {
        return findOne(FIND_BY_ID, id);
    }

    public List<FilmGenre> getGenres() {
        return findMany(FIND_ALL);
    }
}