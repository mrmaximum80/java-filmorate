package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.*;
import java.util.*;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private int idCounter = 0;
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        for (Film f : films.values()) {
            if (film.equals(f)) {
                log.error("Фильм [" + film.getName() + "] уже есть в списке");
                throw new AlreadyExistException("Фильм '" + film.getName() + "' уже есть в списке");
            }
        }
        idCounter++;
        film.setId(idCounter);
        films.put(idCounter, film);
        log.info("Фильм {} добавлен", film.getName());
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Фильм {} изменен", film.getName());
        } else {
            throw new NotFoundException("{\"message\": \"Фильм с id=" + film.getId() + " не найден\"}");
        }
        return film;
    }

}
