package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private int idCounter = 0;
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        for (User u : users.values()) {
            if (u.equals(user)) {
                log.error("Пользователь уже есть в списке");
                throw new FilmAlreadyExistException("Пользователь уже есть в списке");
            }
        }
        idCounter++;
        user.setId(idCounter);
        users.put(idCounter, user);
        log.info("Пользователь {} добавлен", user.getName());
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (users.containsKey(user.getId())) {
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
                log.info("Пользователь {} изменен", user.getName());
            }
            users.put(user.getId(), user);
        } else {
            throw new FilmNotFoundException("{\"message\": \"Пользователь с id=" + user.getId() + " не найден\"}");
        }
        return user;
    }
}
