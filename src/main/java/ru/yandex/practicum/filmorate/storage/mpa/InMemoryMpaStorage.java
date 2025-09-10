package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryMpaStorage implements MpaStorage {

    private final Map<Long, Mpa> mpas = new HashMap<>();

    public Collection<Mpa> getAllRatings() {
        return mpas.values();
    }

    public Optional<Mpa> getMpa(Long id) {
        return mpas.values().stream()
                .filter(mpa -> mpa.getId().equals(id))
                .findFirst();
    }

}