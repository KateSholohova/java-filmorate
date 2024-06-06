package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        log.info("Создание нового пользователя: {}", user);
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("Некорректный email: {}", user.getEmail());
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.error("Некорректный логин: {}", user.getLogin());
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Имя не указано. Используем логин в качестве имени: {}", user.getLogin());
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Некорректная дата рождения: {}", user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем");
        }

        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь создан: {}", user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {

        if (newUser.getId() == null) {
            log.error("Нет id");
            throw new ValidationException("Id должен быть указан");
        }
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            if (newUser.getEmail() != null && (newUser.getEmail().isBlank() || !newUser.getEmail().contains("@"))) {
                log.error("Некорректный email: {}", newUser.getEmail());
                throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
            }
            if (newUser.getLogin() != null && newUser.getLogin().isBlank()) {
                log.error("Некорректный логин: {}", newUser.getLogin());
                throw new ValidationException("Логин не может быть пустым и содержать пробелы");
            }
            if (newUser.getBirthday() != null && newUser.getBirthday().isAfter(LocalDate.now())) {
                log.error("Некорректная дата рождения: {}", newUser.getBirthday());
                throw new ValidationException("Дата рождения не может быть в будущем");
            }
            if (newUser.getName() != null) {
                oldUser.setName(newUser.getName());
            }
            if (newUser.getBirthday() != null) {
                oldUser.setBirthday(newUser.getBirthday());
            }
            if (newUser.getEmail() != null) {
                oldUser.setEmail(newUser.getEmail());
            }
            if (newUser.getLogin() != null) {
                oldUser.setLogin(newUser.getLogin());
            }
            log.info("Пользователь обновлен: {}", oldUser);
            return oldUser;
        }
        log.error("Нет пользователя с данным id: {}", newUser.getId());
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }


    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
