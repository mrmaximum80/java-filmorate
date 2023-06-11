package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;
import ru.yandex.practicum.filmorate.annotation.NonSpaces;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = "id")
@AllArgsConstructor
public class User {
    private long id;

    @NotEmpty
    @Email(message = "Email должен иметь формат адреса электронной почты")
    private String email;

    @NonSpaces
    private String login;

    private String name;

    @NotNull
    @PastOrPresent(message = "День рождения должен содержать прошедшую дату")
    private LocalDate birthday;

    @JsonIgnore
    private Set<Long> friends = new HashSet<>();

}
