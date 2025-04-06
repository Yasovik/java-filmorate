package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {

    @Autowired
    private final FilmService filmService;

    @GetMapping
    public ResponseEntity<List<Film>> allFilms() {
        return ResponseEntity.ok(filmService.getAllFilms());
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
    public ResponseEntity<List<Film>> addLike(@PathVariable("id") int filmId, @PathVariable int userId) {
        log.info("Попытка поставить лайк фильму c id {}", filmId);
        filmService.addLike(userId, filmId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<List<Film>> deleteLike(@PathVariable("id") int filmId, @PathVariable int userId) {
        log.info("Попытка удалить лайк фильма c id {}", filmId);
        filmService.deleteLike(userId, filmId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/popular")
    public ResponseEntity<List<Film>> getPopularFilms(@RequestParam(required = false, defaultValue = "10") int count) {
        if (count < 1) {
            throw new IllegalArgumentException("Параметр count не может быть меньше 1");
        }
        log.info("Запрошен список {} популярных фильмов", count);
        return ResponseEntity.ok(filmService.getTopFilms(count));
    }
}