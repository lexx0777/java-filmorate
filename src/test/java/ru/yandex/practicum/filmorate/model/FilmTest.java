package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmTest {
    private final Validator validator;

    public FilmTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void createValidFilm() {
        Film film = new Film();
        film.setName("Valid Film");
        film.setDescription("A valid film description");
        film.setReleaseDate(LocalDate.of(1995, 12, 28));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty(), "No violations should be present for valid film");
    }

    @Test
    void validateNameNotBlank() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Violations should be present for blank name");
        assertEquals(1, violations.size());
        assertEquals("Название фильма не может быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    void validateDescriptionSize() {
        Film film = new Film();
        film.setName("Film Name");
        film.setDescription("A".repeat(201)); // 201 characters
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Violations should be present for long description");
        assertEquals(1, violations.size());
        assertEquals("Максимальная длина описания — 200 символов", violations.iterator().next().getMessage());
    }

    @Test
    void validateReleaseDateNotNull() {
        Film film = new Film();
        film.setName("Film Name");
        film.setDescription("Description");
        film.setReleaseDate(null);
        film.setDuration(100);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Violations should be present for null release date");
        assertEquals(1, violations.size());
        assertEquals("Дата релиза обязательна", violations.iterator().next().getMessage());
    }

    @Test
    void validateReleaseDateBoundary() {
        Film film = new Film();
        film.setName("Film Name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1895, 12, 27)); // One day before cinema was invented
        film.setDuration(100);

        assertThrows(ValidationException.class, film::validate,
                "Should throw ValidationException for release date before 1895-12-28");
    }

    @Test
    void validateReleaseDateExactBoundary() {
        Film film = new Film();
        film.setName("Film Name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1895, 12, 28)); // Exact boundary
        film.setDuration(100);

        assertDoesNotThrow(film::validate,
                "Should not throw ValidationException for exact boundary release date");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -100})
    void validateDurationPositive(int duration) {
        Film film = new Film();
        film.setName("Film Name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(duration);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Violations should be present for non-positive duration");
        assertEquals(1, violations.size());
        assertEquals("Продолжительность фильма должна быть положительным числом", violations.iterator().next().getMessage());
    }

    @Test
    void validateDurationBoundary() {
        Film film = new Film();
        film.setName("Film Name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(1); // Minimum positive duration

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty(), "No violations should be present for minimal positive duration");
    }
}