package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.annotation.After;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(exclude = "id")
public class Film {

    private static final LocalDate DATE = LocalDate.of(1985, 12, 28);

    private long id;

    @NotBlank(message = "Имя не должно быть пустым")
    private String name;

    @Size(max = 200, message = "Длина описания должна быть не более 200 символов")
    private String description;


    @After(value = "1895-12-28")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительной")
    private int duration;

    @JsonIgnore
    private Set<Long> likes = new HashSet<>();

    public int getRate() {
        return likes.size();
    }
}
