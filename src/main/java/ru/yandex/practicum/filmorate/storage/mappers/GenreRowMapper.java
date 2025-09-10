package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class GenreRowMapper implements RowMapper<FilmGenre> {

    @Override
    public FilmGenre mapRow(final ResultSet rs, final int rowNum) throws SQLException {
        final FilmGenre filmGenre = new FilmGenre();
        filmGenre.setId(rs.getLong("id"));
        filmGenre.setName(rs.getString("title"));

        return filmGenre;
    }
}