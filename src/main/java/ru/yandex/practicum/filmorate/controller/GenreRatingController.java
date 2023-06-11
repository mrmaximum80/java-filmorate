package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.genre.GenreService;
import ru.yandex.practicum.filmorate.service.raring.RatingService;

import java.util.List;

@RestController
public class GenreRatingController {

    private final GenreService genreService;
    private final RatingService ratingService;

    @Autowired
    public GenreRatingController(GenreService genreService, RatingService ratingService) {
        this.genreService = genreService;
        this.ratingService = ratingService;
    }

    @GetMapping("/genres")
    public List<Genre> getGenres() {
        return genreService.getGenres();
    }

    @GetMapping("/genres/{id}")
    public Genre getGenreById(@PathVariable int id) {
        return genreService.getGenreById(id);
    }

    @GetMapping("/mpa")
    public List<Mpa> getRatings() {
        return ratingService.getRatings();
    }

    @GetMapping("/mpa/{id}")
    public Mpa getRatingById(@PathVariable int id) {
        return ratingService.getRatingById(id);
    }

}
