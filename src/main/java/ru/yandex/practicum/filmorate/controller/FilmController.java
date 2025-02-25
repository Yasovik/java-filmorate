package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> allFilms() {
        log.info("Запрошен список всех фильмов, в списке {} фильмов", films.size());
        return films.values();
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        try {
            validation(film);
            film.setId(getNextId());
            films.put(film.getId(), film);
            log.info("Создан фильм с названием {}", film.getName());
            return film;
        } catch (ValidationException exception) {
            log.error("Ошибка при создании фильма: {}", exception.getMessage());
            throw exception;
        }
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film newFilm) {
        try {
            if (!films.containsKey(newFilm.getId())) {
                throw new ValidationException("Пользователя с таким ид не существует");
            }
            validation(newFilm);
            films.put(newFilm.getId(), newFilm);
            log.info("Обновлен фильм с названием {}", newFilm.getName());
            return newFilm;
        } catch (ValidationException exception) {
            log.error("Ошибка при обновлении фильма: {}", exception.getMessage());
            throw exception;
        }
    }

    public void validation(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название не можеть быть пустым");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания — 200 символов;");
        }
        if (LocalDate.of(1895, 12, 28).isAfter(film.getReleaseDate())) {
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() < 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом.");
        }
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