package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.practicum.filmorate.annotation.After;

import javax.validation.constraints.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@EqualsAndHashCode(exclude = "id")
public class Film {

//    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
//    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final LocalDate DATE = LocalDate.of(1985, 12, 28);

    private int id;

    @NotBlank(message = "Имя не должно быть пустым")
    private String name;

    @Size(max = 200, message = "Длина описания должна быть не более 200 символов")
    private String description;

//    @After(value = "28-12-1895",
//            message = "Дата релиза должна быть не раньше 28 декабря 1895 года")
    @After(value = "1895-12-28")
//    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительной")
    private int duration;
}
