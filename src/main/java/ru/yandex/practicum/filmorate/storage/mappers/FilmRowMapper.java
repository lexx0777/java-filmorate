package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import static ru.yandex.practicum.filmorate.storage.utils.DbStorageUtil.*;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmRowMapper implements RowMapper<Film> {

    @Override
    public Film mapRow(final ResultSet rs, final int rowNum) throws SQLException {
        final Film film = new Film();
        film.setId(rs.getLong("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(getNullableLocalDate(rs, "release_date"));
        film.setDuration(getNullableInt(rs, "duration"));
        film.setMpa(getNullableMpa(rs, "mpa_id", "mpa_title"));
        film.setGenres(getNullableFilmGenres(rs, "genres"));
        film.setUsersLikes(getNullableLongSet(rs, "likes"));

        return film;
    }
}