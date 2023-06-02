package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.util.*;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUserById(long id) {
        Optional<User> user = Optional.ofNullable(userStorage.getUserById(id));
        return user.orElseThrow(() -> new NotFoundException("{\"message\": \"Пользователь с id=" + id + " не найден\"}"));
    }

    public User addUser(User user) {
        checkUserName(user);
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        checkUserName(user);
        return userStorage.updateUser(user);
    }

    private void checkUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }


    public User addFriend(long id, long friendId) {
        User user = getUserById(id);
        User friend = getUserById(friendId);
        if (user.getFriends().contains(friendId)) {
            log.info("Пользователь c id={} уже есть в списке друзей", id);
            throw new AlreadyExistException("Пользователь уже есть в списке друзей");
        } else {
            user.getFriends().add(friendId);
            friend.getFriends().add(id);
            log.info("Пользователи c id={} и {} теперь друзья", id, friendId);
        }
        return user;
    }

    public User deleteFriend(long id, long friendId) {
        User user = getUserById(id);
        User friend = getUserById(friendId);
        if (!user.getFriends().contains(friendId)) {
            log.info("Пользователь c id={} отсутствует в списке друзей", id);
            throw new NotFoundException("Пользователь отсутствует в списке друзей");
        } else {
            user.getFriends().remove(friendId);
            friend.getFriends().remove(id);
            log.info("Пользователи c id={} и {} больше не друзья", id, friendId);
        }
        return user;
    }

    public List<User> getUserFriends(long id) {
        Set<Long> friendIds = userStorage.getUserById(id).getFriends();
        List<User> friends = new ArrayList<>();

        for (long i : friendIds) {
            friends.add(userStorage.getUserById(i));
        }
        log.info("Получен список друзей пользователя c id={}.", id);
        return friends;
    }

    public List<User> getCommonFriends(long id, long otherId) {
        Set<Long> userFriendsId = userStorage.getUserById(id).getFriends();
        Set<Long> commonFriendsId = new HashSet<>(userFriendsId);
        Set<Long> otherUserFriendsId = userStorage.getUserById(otherId).getFriends();
        commonFriendsId.retainAll(otherUserFriendsId);

        List<User> commonFriends = new ArrayList<>();

        for (long i : commonFriendsId) {
            commonFriends.add(userStorage.getUserById(i));
        }

        log.info("Получен список общих друзей пользователей c id={} и {}.", id, otherId);
        return commonFriends;
    }
}
