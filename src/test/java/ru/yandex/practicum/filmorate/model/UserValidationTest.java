package ru.yandex.practicum.filmorate.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.yandex.practicum.filmorate.controller.UserController;
import org.junit.jupiter.api.Test;

import javax.validation.Valid;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;

@SpringBootTest
public class UserValidationTest {
//
//    @Autowired
//    UserController userController;
//
//    @Test
//    void badNameTest() {
//        User user = new User();
//        user.setEmail("emailemail.ru");
////        assertThrows(MethodArgumentNotValidException.class, () ->  user.setEmail("emailemail.ru"));
//        user.setLogin("");
//        user.setName("name");
//        user.setBirthday(LocalDate.of(1990,12,12));
//
//        User user1 = userController.addUser(user);
//        assertEquals(1, user1.getId());
//        assertEquals("emailemail.ru", user1.getEmail());
//        System.out.println(user1);
//
//    }
}
