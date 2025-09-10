package ru.yandex.practicum.filmorate.storage.utils;

import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Array;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class DbStorageUtil {

    public static Long getNullableLong(ResultSet rs, String columnName) throws SQLException {
        long value = rs.getLong(columnName);
        return rs.wasNull() ? null : value;
    }

    public static Integer getNullableInt(ResultSet rs, String columnName) throws SQLException {
        int value = rs.getInt(columnName);
        return rs.wasNull() ? null : value;
    }

    public static LocalDate getNullableLocalDate(ResultSet rs, String columnName) throws SQLException {
        Date date = rs.getDate(columnName);
        return date == null ? null : date.toLocalDate();
    }

    public static Mpa getNullableMpa(ResultSet rs, String mpaIdColumn, String mpaTitleColumn) throws SQLException {
        Mpa mpa = new Mpa();
        mpa.setId(getNullableLong(rs, mpaIdColumn));
        mpa.setName(rs.getString(mpaTitleColumn));
        return mpa.getId() == null ? null : mpa;
    }

    public static LinkedHashSet<FilmGenre> getNullableFilmGenres(ResultSet rs, String genresColumn) throws SQLException {
        Array genresArray = rs.getArray(genresColumn);

        if (genresArray == null) return new LinkedHashSet<>();

        Object[] genreStrings = (Object[]) genresArray.getArray();
        LinkedHashSet<FilmGenre> genres = new LinkedHashSet<>();

        for (Object genreString : genreStrings) {
            if (genreString == null) continue;
            FilmGenre filmGenre = new FilmGenre();
            String genre = genreString.toString();
            String[] parts = genre.split(":");
            Long id = Long.valueOf(parts[0]);
            String name = parts[1];
            filmGenre.setId(id);
            filmGenre.setName(name);
            genres.add(filmGenre);
        }
        return genres;
    }

    public static List<Long> getNullableLongList(ResultSet rs, String columnName) throws SQLException {
        Array array = rs.getArray(columnName);
        if (array == null) return List.of();

        Object[] objects = (Object[]) array.getArray();
        return Arrays.stream(objects)
                .map(obj -> ((Number) obj).longValue())
                .toList();
    }

    public static Set<Long> getNullableLongSet(ResultSet rs, String columnName) throws SQLException {
        Array array = rs.getArray(columnName);
        if (array == null) return Set.of();

        Object[] objects = (Object[]) array.getArray();
        return Arrays.stream(objects)
                .map(obj -> ((Number) obj).longValue())
                .collect(Collectors.toSet());
    }


}