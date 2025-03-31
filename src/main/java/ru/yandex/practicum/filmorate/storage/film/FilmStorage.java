package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> allFilms();

    Film findFilm(int filmId);

    Film updateFilm(Film newFilm);

    void validation(Film film);

    Film createFilm(Film film);

    List<Film> getTopFilms(int limit);

    long getNextId();
}
