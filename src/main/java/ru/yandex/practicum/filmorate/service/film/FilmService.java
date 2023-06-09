package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilmById(long id) {
        Optional<Film> film = filmStorage.getFilmById(id);
        return film.orElseThrow(() -> new NotFoundException("{\"message\": \"Фильм с id=" + id + " не найден\"}"));
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film addLike(long id, long userId) {
        Film film = getFilmById(id);
        User user = userService.getUserById(userId);

        if (film.getLikes().contains(userId)) {
            log.info("Пользователя c id={} уже поставил лайк фильму", id);
            throw new AlreadyExistException("Пользователь с id=" + userId + " уже поставил лайк фильму.");
        } else {
            filmStorage.addLike(id, userId);
            log.info("Фильму с id={} добавлен лайк пользователя c id={}", id, userId);
        }
        return getFilmById(id);
    }

    public Film deleteLike(long id, long userId) {
        Film film = getFilmById(id);
        User user = userService.getUserById(userId);
        if (!film.getLikes().contains(userId)) {
            log.info("Пользователь c id={} не ставил лайк фильму c id={}", userId, id);
            throw new NotFoundException("Пользователь с id=" + userId + " не ставил лайк фильму с id=" + id + ".");
        } else {
            filmStorage.deleteLike(id, userId);
            log.info("У фильма с id={} удален лайк пользователя c id={}", id, userId);
        }
        return film;
    }

    public List<Film> getPopularFilms(int count) {
        List<Film> popularFilms = getFilms().stream().sorted(new Comparator<Film>() {
            @Override
            public int compare(Film o1, Film o2) {
                return o2.getRate() - o1.getRate();
            }
        }).limit(count).collect(Collectors.toList());
        log.info("Создан список из {} популярных фильмов", count);
        return popularFilms;
    }
}
