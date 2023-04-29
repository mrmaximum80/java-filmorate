package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.annotation.NonSpaces;

import javax.validation.constraints.Email;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = "id")
@AllArgsConstructor
public class User {
    private int id;

    @Email(message = "Email должен иметь формат адреса электронной почты")
    private String email;

    @NonSpaces
    private String login;

    private String name;

    @Past(message = "День рождения должен содержать прошедшую дату")
    private LocalDate birthday;

}
