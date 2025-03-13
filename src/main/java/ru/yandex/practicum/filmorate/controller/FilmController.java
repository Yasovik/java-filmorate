package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    FilmService filmService;

    @Autowired
    public FilmController(FilmService service) {
        this.filmService = service;
    }

    @GetMapping
    public ResponseEntity<Collection<Film>> allFilms() {
        return ResponseEntity.ok(filmService.allFilms());
    }

    @PostMapping
    public ResponseEntity<?> createFilm(@Valid @RequestBody Film film) {
        filmService.createFilm(film);
        return ResponseEntity.ok(film);
    }

    @PutMapping
    public ResponseEntity<?> updateFilm(@Valid @RequestBody Film newFilm) {
        filmService.updateFilm(newFilm);
        return ResponseEntity.ok(newFilm);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFilm(@PathVariable int id) {
        return ResponseEntity.ok(filmService.findFilm(id));
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Collection<Film>> addLike(@PathVariable("id") int filmId, @PathVariable int userId) {
        log.info("Попытка поставить лайк фильму");
        filmService.addLike(userId, filmId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Collection<Film>> deleteLike(@PathVariable("id") int filmId, @PathVariable int userId) {
        log.info("Попытка удалить лайк фильма");
        filmService.deleteLike(userId, filmId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/popular")
    public ResponseEntity<Collection<Film>> getPopularFilms(@RequestParam(required = false, defaultValue = "10") int count) {
        if (count < 1) {
            throw new NotFoundException("Параметр count не может быть меньше 1");
        }
        log.info("Запрошен список {} популярных фильмов", count);
        return ResponseEntity.ok(filmService.getTopFilms(count));
    }
}