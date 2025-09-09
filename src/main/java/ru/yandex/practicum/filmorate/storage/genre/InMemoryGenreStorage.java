package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryGenreStorage implements GenreStorage {

    private final Map<Long, FilmGenre> genres = new HashMap<>();

    public Optional<FilmGenre> getGenre(Long id) {
        return genres.values().stream()
                .filter(genre -> genre.getId().equals(id))
                .findFirst();
    }

    public Collection<FilmGenre> getGenres() {
        return genres.values();
    }
}