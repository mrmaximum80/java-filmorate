package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private long idCounter = 0;
    private final Map<Long, User> users = new HashMap<>();


    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User addUser(User user) {
        for (User u : users.values()) {
            if (u.equals(user)) {
                log.error("Пользователь уже есть в списке");
                throw new AlreadyExistException("Пользователь уже есть в списке");
            }
        }

        user.setId(++idCounter);
        users.put(idCounter, user);
        log.info("Пользователь {} добавлен", user.getName());
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.info("Пользователь {} изменен", user.getName());
        } else {
            throw new NotFoundException("{\"message\": \"Пользователь с id=" + user.getId() + " не найден\"}");
        }
        return user;
    }

    @Override
    public User getUserById(long id) {
        User user = users.get(id);
        if (user == null) {
            log.info("Пользователь с id={} не найден", id);
        } else {
            log.info("Пользователь с id={} найден", id);
        }
        return user;
    }

}
