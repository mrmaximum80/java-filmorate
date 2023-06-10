package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbStorageTest {

    private final UserDbStorage userStorage;

    @Test
    public void userDbStorageTest() {

        User user = new User();
        user.setLogin("Login");
        user.setEmail("mail@mail.ru");
        user.setName("Name");
        user.setBirthday(LocalDate.of(1980, 12, 01));

        userStorage.addUser(user);

        Optional<User> userOptional = userStorage.getUserById(1);
        User savedUser = userOptional.get();

        assertEquals(user.getName(), savedUser.getName());
        assertEquals(user.getLogin(), savedUser.getLogin());
        assertEquals(user.getEmail(), savedUser.getEmail());
        assertEquals(user.getBirthday(), savedUser.getBirthday());
        assertEquals(savedUser.getId(), 1);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(us ->
                        assertThat(us).hasFieldOrPropertyWithValue("id", 1L)
                );

        User updatedUser = new User();
        updatedUser.setId(1);
        updatedUser.setLogin("LoginUp");
        updatedUser.setEmail("mailUp@mail.ru");
        updatedUser.setName("NameUp");
        updatedUser.setBirthday(LocalDate.of(1990, 12, 01));

        userStorage.updateUser(updatedUser);

        userOptional = userStorage.getUserById(1);
        savedUser = userOptional.get();

        assertEquals(updatedUser.getName(), savedUser.getName());
        assertEquals(updatedUser.getLogin(), savedUser.getLogin());
        assertEquals(updatedUser.getEmail(), savedUser.getEmail());
        assertEquals(updatedUser.getBirthday(), savedUser.getBirthday());
        assertEquals(savedUser.getId(), 1);

        User user2 = new User();
        user2.setLogin("Login2");
        user2.setEmail("mail2@mail.ru");
        user2.setName("Name2");
        user2.setBirthday(LocalDate.of(1970, 12, 01));

        userStorage.addUser(user2);
        List<User> users = userStorage.getUsers();
        savedUser = users.get(1);

        assertEquals(2, users.size());

        assertEquals(user2.getName(), savedUser.getName());
        assertEquals(user2.getLogin(), savedUser.getLogin());
        assertEquals(user2.getEmail(), savedUser.getEmail());
        assertEquals(user2.getBirthday(), savedUser.getBirthday());
        assertEquals(user2.getId(), 2);

        userStorage.addFriend(1, 2);

        savedUser = userStorage.getUserById(1).get();

        assertEquals(savedUser.getFriends().size(), 1);
        assertTrue(savedUser.getFriends().contains(2L));

        userStorage.deleteFriend(1, 2);
        savedUser = userStorage.getUserById(1).get();
        assertEquals(savedUser.getFriends().size(), 0);
    }

}
