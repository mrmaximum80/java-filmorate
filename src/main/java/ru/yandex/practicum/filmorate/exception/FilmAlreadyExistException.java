package ru.yandex.practicum.filmorate.exception;

import ru.yandex.practicum.filmorate.model.Film;

public class FilmAlreadyExistException extends RuntimeException {

    public FilmAlreadyExistException(String message) {
        super(message);
    }
}
