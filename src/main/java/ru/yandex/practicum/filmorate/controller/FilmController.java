package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.info("Создание нового фильм: {}", film);
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Некорректное имя: {}", film.getName());
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.error("Некорректное описание: {}", film.getDescription().length());
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.parse("1895-12-28"))) {
            log.error("Некорректная дата выхода: {}", film.getReleaseDate());
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() < 0) {
            log.error("Некорректная продолжительность: {}", film.getDuration());
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }

        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм создан: {}", film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {

        if (newFilm.getId() == null) {
            log.error("Нет id");
            throw new ValidationException("Id должен быть указан");
        }
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            if (newFilm.getName() != null && newFilm.getName().isBlank()) {
                log.error("Некорректное имя: {}", newFilm.getName());
                throw new ValidationException("Название не может быть пустым");
            }
            if (newFilm.getDescription() != null && newFilm.getDescription().length() > 200) {
                log.error("Некорректное описание: {}", newFilm.getDescription().length());
                throw new ValidationException("Максимальная длина описания — 200 символов");
            }
            if (newFilm.getReleaseDate() != null && newFilm.getReleaseDate().isBefore(LocalDate.parse("1895-12-28"))) {
                log.error("Некорректная дата выхода: {}", newFilm.getReleaseDate());
                throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
            }
            if (newFilm.getDuration() != null && newFilm.getDuration() < 0) {
                log.error("Некорректная продолжительность: {}", newFilm.getDuration());
                throw new ValidationException("Продолжительность фильма должна быть положительным числом");
            }

            if (newFilm.getName() != null) {
                oldFilm.setName(newFilm.getName());
            }
            if (newFilm.getDescription() != null) {
                oldFilm.setDescription(newFilm.getDescription());
            }
            if (newFilm.getDuration() != null) {
                oldFilm.setDuration(newFilm.getDuration());
            }
            if (newFilm.getReleaseDate() != null) {
                oldFilm.setReleaseDate(newFilm.getReleaseDate());
            }


            log.info("Фильм обновлен: {}", oldFilm);
            return oldFilm;
        }
        log.error("Нет фильма с данным id: {}", newFilm.getId());
        throw new NotFoundException("Пост с id = " + newFilm.getId() + " не найден");
    }


    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
