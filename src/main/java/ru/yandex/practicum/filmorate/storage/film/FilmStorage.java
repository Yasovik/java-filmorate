package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FilmStorage {
    List<Film> allFilms();

    Film findFilm(int filmId);

    Film updateFilm(Film newFilm);

    Film createFilm(Film film);

    List<Film> getTopFilms(int limit);

    void addLike(Long id, Long userId);

    void removeLike(Long id, Long userId);

    List<User> getLikes(Long filmId);
}
