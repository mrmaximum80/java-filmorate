package ru.yandex.practicum.filmorate.exception;

import javax.validation.constraints.NotBlank;

public class FilmNotFoundException extends RuntimeException {
    public FilmNotFoundException(String message) {
        super(message);
    }
}
