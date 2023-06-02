package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.annotation.NonSpaces;

import javax.validation.constraints.Email;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = "id")
@AllArgsConstructor
public class User {
    private long id;

    @Email(message = "Email должен иметь формат адреса электронной почты")
    private String email;

    @NonSpaces
    private String login;

    private String name;

    @Past(message = "День рождения должен содержать прошедшую дату")
    private LocalDate birthday;

    private Set<Long> friends = new HashSet<>();

}
