package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserControllerTest {

    @Autowired
    UserController userController;

    @Test
    void userEmptyNameTest() {
        User user = new User();
        user.setEmail("email@email.ru");
        user.setLogin("login1");
        user.setName("");
        user.setBirthday(LocalDate.of(1990,12,12));

        assertEquals("login1", userController.addUser(user).getName(), "Имя должно быть равно логину.");

        User otherUser = new User();
        otherUser.setEmail("email2@email.ru");
        otherUser.setName(null);
        otherUser.setLogin("login2");
        otherUser.setBirthday(LocalDate.of(1990,12,12));

        assertEquals("login2", userController.addUser(otherUser).getName(), "Имя должно быть равно логину.");
    }

}