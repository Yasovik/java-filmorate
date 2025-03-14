package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public List<Film> getAllFilms() {
        return filmStorage.allFilms();
    }

    public Film findFilm(int filmId) {
        return filmStorage.findFilm(filmId);
    }

    public Film updateFilm(Film newFilm) {
        return filmStorage.updateFilm(newFilm);
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public List<Film> getTopFilms(int limit) {
        log.info("Получены лучшие {} фильмов", limit);
        return filmStorage.getTopFilms(limit);
    }

    public void addLike(int userId, int filmId) {
        log.info("Пользователь {} пытается поставить лайк фильму {}", userId, filmId);
        try {
            userStorage.findUser(userId);//выбросит исключение
            Film film = filmStorage.findFilm(filmId);
            film.addLike(userId);
            log.info("Лайк поставлен успешно");
        } catch (NotFoundException e) {
            log.warn("Поставить лайк не удалось: {}", e.getMessage());
            throw e;
        }
    }

    public void deleteLike(int userId, int filmId) {
        log.info("Пользователь {} пытается удалить лайк у фильма {}", userId, filmId);
        try {
            userStorage.findUser(userId);//выбросит исключение
            Film film = filmStorage.findFilm(filmId);
            film.deleteLike(userId);
            log.info("Лайк удален успешно");
        } catch (NotFoundException e) {
            log.warn("Удалить лайк не удалось: {}", e.getMessage());
            throw e;
        }
    }

}
