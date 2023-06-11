package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import java.util.*;

@Component("userDbStorage")
@Slf4j
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getUsers() {
        String sql = "select * from users order by id";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sql);
        List<User> users = new ArrayList<>();
        while (userRows.next()) {
            User user = new User();
            user.setId(userRows.getLong("id"));
            user.setEmail(userRows.getString("email"));
            user.setLogin(userRows.getString("login"));
            user.setName(userRows.getString("name"));
            user.setBirthday(userRows.getDate("birthday").toLocalDate());
            user.setFriends(getUserFriendsIds(user.getId()));
            users.add(user);
        }
        return users;
    }

    @Override
    public User addUser(User user) {

        if (jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users WHERE email = ? OR login = ?",Integer.class,
                user.getEmail(), user.getLogin()) != 0) {
            log.error("Пользователь с таким email/login уже есть в списке");
                throw new AlreadyExistException("Пользователь с таким email/login уже есть в списке");
        }

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
        long id = simpleJdbcInsert.executeAndReturnKey(userToMap(user)).longValue();
        log.info("Пользователь {} добавлен", user.getName());

        Set<Long> friendIds = user.getFriends();
        for (long friendId : friendIds) {
            jdbcTemplate.update("insert into friends (user_id, friend_id) values (?, ?)", id, friendId);
        }
        log.info("Друзья пользователя {} добавлены", user.getName());
        user.setId(id);

        return user;
    }

    @Override
    public User updateUser(User user) {
        Optional<User> userForUpdate = getUserById(user.getId());
        if (userForUpdate.isPresent()) {
            if (jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users WHERE email = ? OR login = ? AND id != ?",
                    Integer.class,
                    user.getEmail(), user.getLogin(), user.getId()) != 0) {
                log.error("Пользователь с таким email/login уже есть в списке");
                throw new AlreadyExistException("Пользователь с таким email/login уже есть в списке");
            }

            String sql = "update users set email = ?, login = ?, name = ?, birthday = ? where id = ?;";
            jdbcTemplate.update(sql,
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    user.getBirthday(),
                    user.getId());
            log.info("Пользователь {} изменен", user.getName());

            long id = user.getId();
            jdbcTemplate.update("delete from friends where user_id = ?", id);

            Set<Long> friendIds = user.getFriends();

            for (long friendId : friendIds) {
                jdbcTemplate.update("insert into friends (user_id, friend_id) values (?, ?)", id, friendId);
            }
            log.info("Друзья пользователя {} добавлены", user.getName());

        } else {
            throw new NotFoundException("{\"message\": \"Пользователь с id=" + user.getId() + " не найден\"}");
        }
        return user;
    }

    @Override
    public Optional<User> getUserById(long id) {
        String sql = "select * from users where id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sql, id);
        if (userRows.next()) {
            User user = new User();
            user.setId(userRows.getLong("id"));
            user.setEmail(userRows.getString("email"));
            user.setLogin(userRows.getString("login"));
            user.setName(userRows.getString("name"));
            user.setBirthday(userRows.getDate("birthday").toLocalDate());
            user.setFriends(getUserFriendsIds(id));
            log.info("Найден пользователь: {} {}", user.getId(), user.getName());
            return Optional.of(user);
        }
        log.info("Пользователь с идентификатором {} не найден.", id);
        return Optional.empty();
    }

    @Override
    public void addFriend(long userId, long friendId) {
        jdbcTemplate.update("insert into friends (user_id, friend_id) values (?, ?)", userId, friendId);
        log.info("Пользователю с id = {} добавлен друг с id = {}.", userId, friendId);
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        jdbcTemplate.update("delete from friends where user_id = ? and friend_id = ?", userId, friendId);
        log.info("У пользователя с id = {} удален друг с id = {}.", userId, friendId);
    }

    private Set<Long> getUserFriendsIds(long id) {
        String sql = "select friend_id from friends where user_id = ?";
        return new HashSet<>(jdbcTemplate.queryForList(sql, Long.class, id));
    }

    private Map<String, Object> userToMap(User user) {
        Map<String, Object> values = new HashMap<>();
        values.put("email", user.getEmail());
        values.put("login", user.getLogin());
        values.put("name", user.getName());
        values.put("birthday", user.getBirthday());
        return values;
    }
}
