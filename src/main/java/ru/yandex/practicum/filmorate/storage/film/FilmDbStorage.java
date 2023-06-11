package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;

import java.util.*;

@Component("filmDbStorage")
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> getFilms() {
        String sql = "select * from films order by id";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sql);
        List<Film> films = new ArrayList<>();
        while (filmRows.next()) {
            Film film = new Film();
            film.setId(filmRows.getLong("id"));
            film.setName(filmRows.getString("name"));
            film.setDescription(filmRows.getString("description"));
            film.setReleaseDate(filmRows.getDate("release_date").toLocalDate());
            film.setDuration(filmRows.getInt("duration"));
            film.setMpa(getFilmMpa(film.getId()));
            film.setGenres(getFilmGenres(film.getId()));
            film.setLikes(getFilmLikesIds(film.getId()));
            films.add(film);
        }
        return films;
    }

    @Override
    public Film addFilm(Film film) {

        if (jdbcTemplate.queryForObject("SELECT COUNT(*) FROM films WHERE name = ? AND description = ? " +
                        "AND release_date = ? AND duration = ?", Integer.class,
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration()) != 0) {
            log.error("Фильм [" + film.getName() + "] уже есть в списке");
            throw new AlreadyExistException("Фильм '" + film.getName() + "' уже есть в списке");
        }

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("id");
        long id = simpleJdbcInsert.executeAndReturnKey(filmToMap(film)).longValue();
        log.info("Фильм {} добавлен", film.getName());

        Mpa mpa = film.getMpa();
        jdbcTemplate.update("insert into film_mpa (film_id, mpa_id) values (?, ?)", id, mpa.getId());
        film.setMpa(getFilmMpa(id));
        log.info("MPA фильма {} добавлен", film.getName());

        Set<Genre> genresIds = film.getGenres();
        for (Genre genreId : genresIds) {
            jdbcTemplate.update("insert into film_genre (film_id, genre_id) values (?, ?)", id, genreId.getId());
        }
        film.setGenres(getFilmGenres(id));
        log.info("Жанры фильма {} добавлены", film.getName());

        Set<Long> likesIds = film.getLikes();
        for (long likeId : likesIds) {
            jdbcTemplate.update("insert into likes (film_id, user_id) values (?, ?)", id, likeId);
        }
        log.info("Лайки фильма {} добавлены", film.getName());
        film.setId(id);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        Optional<Film> filmForUpdate = getFilmById(film.getId());
        if (filmForUpdate.isPresent()) {
            if (jdbcTemplate.queryForObject("SELECT COUNT(*) FROM films WHERE name = ? AND description = ? " +
                            "AND release_date = ? AND duration = ? AND id != ?", Integer.class,
                    film.getName(), film.getDescription(), film.getReleaseDate(),
                    film.getDuration(), film.getId()) != 0) {
                log.error("Фильм [" + film.getName() + "] уже есть в списке");
                throw new AlreadyExistException("Фильм '" + film.getName() + "' уже есть в списке");
            }

            String sql = "update films set name = ?, description = ?, release_date = ?, duration = ?" +
                    " where id = ?;";
            jdbcTemplate.update(sql,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getId());
            log.info("Пользователь {} изменен", film.getName());

            long id = film.getId();

            jdbcTemplate.update("update film_mpa set mpa_id = ? where film_id = ?", film.getMpa().getId(), id);
            film.setMpa(getFilmMpa(id));
            log.info("MPA фильма {} обновлен", film.getName());

            jdbcTemplate.update("delete from film_genre where film_id = ?", id);
            log.info("Жанры фильма {} удалены", film.getName());

            Set<Genre> genresIds = film.getGenres();
            for (Genre genreId : genresIds) {
                jdbcTemplate.update("insert into film_genre (film_id, genre_id) values (?, ?)", id, genreId.getId());
            }
            log.info("Жанры фильма {} добавлены", film.getName());
            film.setGenres(getFilmGenres(id));

            jdbcTemplate.update("delete from likes where film_id = ?", id);
            log.info("Лайки фильма {} удалены", film.getName());

            Set<Long> likeIds = film.getLikes();

            for (long likeId : likeIds) {
                jdbcTemplate.update("insert into likes (film_id, user_id) values (?, ?)", id, likeId);
            }
            log.info("Лайки фильма {} добавлены", film.getName());

            log.info("Фильм {} изменен", film.getName());
        } else {
            throw new NotFoundException("{\"message\": \"Фильм с id=" + film.getId() + " не найден\"}");
        }
        return film;
    }

    @Override
    public Optional<Film> getFilmById(long id) {
        String sql = "select * from films where id = ?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sql, id);
        if (filmRows.next()) {
            Film film = new Film();
            film.setId(filmRows.getLong("id"));
            film.setName(filmRows.getString("name"));
            film.setDescription(filmRows.getString("description"));
            film.setReleaseDate(filmRows.getDate("release_date").toLocalDate());
            film.setDuration(filmRows.getInt("duration"));
            film.setMpa(getFilmMpa(id));
            film.setGenres(getFilmGenres(id));
            film.setLikes(getFilmLikesIds(id));
            log.info("Найден фильм: {} {}", film.getId(), film.getName());
            return Optional.of(film);
        }
        log.info("Фильм с идентификатором {} не найден.", id);
        return Optional.empty();
    }

    @Override
    public void addLike(long filmId, long userId) {
        jdbcTemplate.update("insert into likes (film_id, user_id) values (?, ?)", filmId, userId);
        log.info("Фильму с id = {} добавлен лайк от пользователя с id = {}.", filmId, userId);
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        jdbcTemplate.update("delete from likes where film_id = ? and user_id = ?", filmId, userId);
        log.info("У фильма с id = {} удален лайк от пользователя с id = {}.", filmId, userId);
    }

    private Mpa getFilmMpa(long id) {
        String sql = "select film_mpa.mpa_id, mpa.name from film_mpa left join mpa on film_mpa.mpa_id = mpa.id " +
                "where film_id = ?";
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet(sql, id);
        if (mpaRows.next()) {
            Mpa mpa = new Mpa();
            mpa.setId(mpaRows.getInt("mpa_id"));
            mpa.setName(mpaRows.getString("name"));
            return mpa;
        }
        return null;
    }

    private Set<Genre> getFilmGenres(long id) {
        String sql = "select genres.id, genres.name from film_genre" +
                " left join genres on film_genre.genre_id = genres.id" +
                " where film_id = ? order by id";
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sql, id);
        Set<Genre> genres = new HashSet<>();
        while (genreRows.next()) {
            Genre genre = new Genre();
            genre.setId(genreRows.getInt("id"));
            genre.setName(genreRows.getString("name"));
            genres.add(genre);
        }
        return genres;
    }

    private Set<Long> getFilmLikesIds(long id) {
        String sql = "select user_id from likes where film_id = ?";
        return new HashSet<>(jdbcTemplate.queryForList(sql, Long.class, id));
    }

    private Map<String, Object> filmToMap(Film film) {
        Map<String, Object> values = new HashMap<>();
        values.put("name", film.getName());
        values.put("description", film.getDescription());
        values.put("release_date", film.getReleaseDate());
        values.put("duration", film.getDuration());
        return values;
    }
}
