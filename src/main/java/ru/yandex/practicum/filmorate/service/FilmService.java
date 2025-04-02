package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;
    private final UserStorage userStorage;

    public List<Film> getAllFilms() {
        return filmStorage.allFilms();
    }

    public Film findFilm(int filmId) {
        return filmStorage.findFilm(filmId);
    }

    public Film updateFilm(Film newFilm) {
        if (newFilm.getId() == null) {
            log.error("нет айди");
            throw new ValidationException("Id должен быть указан");
        }
        if (filmStorage.findFilm(newFilm.getId().intValue()) == null) {
            log.error("такого фильма нет {}", newFilm.getId());
            throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
        }
        return filmStorage.updateFilm(newFilm);
    }

    public Film createFilm(Film film) {
        if (mpaStorage.getById(film.getMpa().getId()) == null) {
            throw new NotFoundException("Рейтинг + " + film.getMpa() + " не найден");
        }
        if (film.getGenres() != null) {
            genreStorage.checkGenresExists(film.getGenres());
        }
        return filmStorage.createFilm(film);
    }

    public List<Film> getTopFilms(int limit) {
        log.info("Получены лучшие {} фильмов", limit);
        return filmStorage.getTopFilms(limit);
    }

    public void addLike(int userId, int filmId) {
        log.info("Пользователь {} пытается поставить лайк фильму {}", userId, filmId);
        if (filmStorage.findFilm(filmId) == null) {
            log.error("ошибка с id фильма  {}", filmId);
            throw new NotFoundException("Фильма с таким id найдено");
        }
        if (userStorage.findUser(userId) == null) {
            log.error("ошибка с id юзера  {}", userId);
            throw new NotFoundException("Юзера с таким id найдено");
        }
        //filmStorage.checkLikeOnFilm(filmId,userId);
        filmStorage.addLike((long) filmId, (long) userId);
    }

    public void deleteLike(int userId, int filmId) {
        log.info("Пользователь {} пытается удалить лайк у фильма {}", userId, filmId);
        if (filmStorage.findFilm(filmId) == null) {
            log.error("ошибка с id фильма  {}", filmId);
            throw new NotFoundException("Фильма с таким id найдено");
        }
        if (userStorage.findUser(userId) == null) {
            log.error("ошибка с id юзера  {}", userId);
            throw new NotFoundException("Юзера с таким id найдено");
        }
        filmStorage.removeLike((long) filmId, (long) userId);
    }

}
