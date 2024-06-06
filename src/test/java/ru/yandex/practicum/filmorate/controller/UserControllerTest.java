package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

class UserControllerTest {
    private static UserController userController;

    @BeforeEach
    public void beforeEach() {
        userController = new UserController();
    }

    @Test
    void testCreateUser() {
        User user = new User();
        user.setEmail(" ");
        assertThrows(ValidationException.class, () -> {
            userController.create(user);
        });
        user.setEmail("email@");
        user.setLogin(" ");
        assertThrows(ValidationException.class, () -> {
            userController.create(user);
        });
        user.setLogin("a");
        user.setBirthday(LocalDate.parse("2027-12-28"));
        assertThrows(ValidationException.class, () -> {
            userController.create(user);
        });
        user.setBirthday(LocalDate.parse("2000-12-28"));
        userController.create(user);

    }
}