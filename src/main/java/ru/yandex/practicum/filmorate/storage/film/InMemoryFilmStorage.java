package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films;

    public InMemoryFilmStorage() {
        films = new HashMap<>();
    }

    @Override
    public List<Film> allFilms() {
        log.info("Запрошен список всех фильмов, в списке {} фильмов", films.size());
        return new ArrayList<>(films.values());
    }

    @Override
    public Film findFilm(int id) {
        log.info("Попытка получить данные о фильме: {}", id);
        if (films.containsKey((long) id)) {
            log.info("Данные о фильме успешно получены");
            return films.get((long) id);
        } else {
            log.warn("Не удалось получить данные");
            throw new NotFoundException("Данный фильм не найден");
        }
    }

    @Override
    public Film createFilm(Film film) {
        validation(film);
        film.setId(getNextId());
        putFilmLikes(film);
        films.put(film.getId(), film);
        log.info("Создан фильм с названием {}", film.getName());
        return film;
    }

    @Override
    public Film updateFilm(Film newFilm) {
        if (!films.containsKey(newFilm.getId())) {
            throw new NotFoundException("Фильма с id " + newFilm.getId() + " не существует");
        }
        validation(newFilm);
        putFilmLikes(newFilm);
        films.put(newFilm.getId(), newFilm);
        log.info("Обновлен фильм с названием {}", newFilm.getName());
        return newFilm;
    }

    public List<Film> getTopFilms(int limit) {
        return films.values().stream()
                .sorted((film1, film2) -> Integer.compare(film2.getLike().size(), film1.getLike().size())) // Сортируем по убыванию количества лайков
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public void validation(Film film) {
        try {
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
        } catch (ValidationException exception) {
            log.error("Ошибка при создании фильма: {}", exception.getMessage());
            throw exception;
        }
    }

    public void putFilmLikes(Film film) {
        if (film.getLike() == null) {
            film.setLike(new HashSet<>());
            log.info("likes Set фильма заменен на пустой HashSet");
        }
    }

    @Override
    public long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
