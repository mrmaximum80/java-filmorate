package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private long idCounter = 0;
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film addFilm(Film film) {
        for (Film f : films.values()) {
            if (film.equals(f)) {
                log.error("Фильм [" + film.getName() + "] уже есть в списке");
                throw new AlreadyExistException("Фильм '" + film.getName() + "' уже есть в списке");
            }
        }

        film.setId(++idCounter);
        films.put(idCounter, film);
        log.info("Фильм {} добавлен", film.getName());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Фильм {} изменен", film.getName());
        } else {
            throw new NotFoundException("{\"message\": \"Фильм с id=" + film.getId() + " не найден\"}");
        }
        return film;
    }

    @Override
    public Film getFilmById(long id) {
        Film film = films.get(id);
        if (film == null) {
            throw new NotFoundException("{\"message\": \"Пользователь с id=" + id + " не найден\"}");
//            log.info("Пользователь с id={} не найден", id);
        }
        log.info("Пользователь с id={} найден", id);
        return film;
    }
}
