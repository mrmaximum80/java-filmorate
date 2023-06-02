package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addFriend(long id, long friendId) {
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);
        if (user.getFriends().contains(friendId)) {
            log.info("Пользователь c id={} уже есть в списке друзей", id);
            throw new AlreadyExistException("Пользователь уже есть в списке друзей");
        } else {
            user.getFriends().add(friendId);
            userStorage.updateUser(user);
            friend.getFriends().add(id);
            userStorage.updateUser(friend);
            log.info("Пользователи c id={} и {} теперь друзья", id, friendId);
        }
        return user;
    }

    public User deleteFriend(long id, long friendId) {
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);
        if (!user.getFriends().contains(friendId)) {
            log.info("Пользователь c id={} отсутствует в списке друзей", id);
            throw new NotFoundException("Пользователь отсутствует в списке друзей");
        } else {
            user.getFriends().remove(friendId);
            userStorage.updateUser(user);
            friend.getFriends().remove(id);
            userStorage.updateUser(friend);
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
