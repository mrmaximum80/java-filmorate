package ru.yandex.practicum.filmorate.service.raring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.rating.MpaDbStorage;

import java.util.List;
import java.util.Optional;

@Service
public class RatingService {

    private final MpaDbStorage mpaStorage;

    @Autowired
    public RatingService(MpaDbStorage ratingStorage) {
        this.mpaStorage = ratingStorage;
    }

    public List<Mpa> getRatings() {
        return mpaStorage.getRatings();
    }

    public Mpa getRatingById(int id) {
        Optional<Mpa> rating = mpaStorage.getRatingById(id);
        return rating.orElseThrow(() -> new NotFoundException("{\"message\": \"Рейтинг с id=" + id + " не найден\"}"));
    }
}
