package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
public class User {
    private Long id;
    private Set<Long> friends = new HashSet<>();

    @Email(message = "Электронная почта пользователя должна соответствовать формату электронного адреса")
    @NotBlank(message = "Электронная почта пользователя не может быть пустой")
    private String email;

    @NotBlank(message = "Логин пользователя не может быть пустым")
    @Pattern(regexp = "\\S+", message = "Логин пользователя не может содержать пробелы")
    private String login;
    private String name;

    @PastOrPresent(message = "Дата рождения пользователя не может быть в будущем")
    private LocalDate birthday;

    public User(Long id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}