package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmControllerTest {

    private static FilmController filmController;

    @BeforeEach
    public void beforeEach() {
        filmController = new FilmController();
    }

    @Test
    void TestCreateFilm() {
        Film film = new Film();
        film.setName(" ");
        assertThrows(ValidationException.class, () -> {
            filmController.create(film);
        });
        film.setName("name");
        film.setDescription("ааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааа");
        assertThrows(ValidationException.class, () -> {
            filmController.create(film);
        });
        film.setDescription("a");
        film.setReleaseDate(LocalDate.parse("1890-12-28"));
        assertThrows(ValidationException.class, () -> {
            filmController.create(film);
        });
        film.setReleaseDate(LocalDate.parse("1900-12-28"));
        film.setDuration(Duration.ofMinutes(-1));
        assertThrows(ValidationException.class, () -> {
            filmController.create(film);
        });
    }

}