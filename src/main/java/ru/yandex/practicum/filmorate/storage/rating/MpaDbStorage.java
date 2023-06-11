package ru.yandex.practicum.filmorate.storage.rating;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class MpaDbStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Mpa> getRatings() {
        SqlRowSet ratingRows = jdbcTemplate.queryForRowSet("select * from mpa order by id");
        List<Mpa> mpas = new ArrayList<>();
        while (ratingRows.next()) {
            Mpa rating = new Mpa();
            rating.setId(ratingRows.getInt("id"));
            rating.setName(ratingRows.getString("name"));
            log.info("Найден рейтинг: {} {}", rating.getId(), rating.getName());
            mpas.add(rating);
        }
        return mpas;
    }

    public Optional<Mpa> getRatingById(int id) {
        String sql = "select * from mpa where id = ?";
        SqlRowSet ratingRows = jdbcTemplate.queryForRowSet(sql, id);
        if (ratingRows.next()) {
            Mpa rating = new Mpa();
            rating.setId(ratingRows.getInt("id"));
            rating.setName(ratingRows.getString("name"));
            log.info("Найден рейтинг: {} {}", rating.getId(), rating.getName());
            return Optional.of(rating);
        }
        log.info("Рейтинг с идентификатором {} не найден.", id);
        return Optional.empty();
    }
}
