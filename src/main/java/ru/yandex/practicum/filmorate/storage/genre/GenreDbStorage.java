package ru.yandex.practicum.filmorate.storage.genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class GenreDbStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Genre> getGenres() {
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("select * from genres order by id");
        List<Genre> genres = new ArrayList<>();
        while (genreRows.next()) {
            Genre genre = new Genre();
            genre.setId(genreRows.getInt("id"));
            genre.setName(genreRows.getString("name"));
            log.info("Найден жанр: {} {}", genre.getId(), genre.getName());
            genres.add(genre);
        }
        return genres;
    }

    public Optional<Genre> getGenreById(int id) {
        String sql = "select * from genres where id = ?";
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sql, id);
        if (genreRows.next()) {
            Genre genre = new Genre();
            genre.setId(genreRows.getInt("id"));
            genre.setName(genreRows.getString("name"));
            log.info("Найден жанр: {} {}", genre.getId(), genre.getName());
            return Optional.of(genre);
        }
        log.info("Жанр с идентификатором {} не найден.", id);
        return Optional.empty();
    }
}
