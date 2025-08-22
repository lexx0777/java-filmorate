package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Film.
 */

@Data
@NoArgsConstructor
public class Film {
    private Long id;
    private Set<Long> usersLikes = new HashSet<>();

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @Size(max = 200, message = "Описание фильма должно быть не длиннее 200 символов")
    private String description;

    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private Integer duration;

    public Film(Long id, String name, String description, LocalDate releaseDate, Integer duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    @AssertTrue(message = "Дата релиза фильма должна быть не раньше 28 декабря 1895 года")
    @JsonIgnore
    public boolean isReleaseDateValid() {
        if (releaseDate == null) {
            return true;
        }
        return releaseDate.isAfter(LocalDate.of(1895, 12, 28));
    }

    public void validate() {
    }
}