package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {

    private FilmController controller;

    @BeforeEach
    public void beforeEach() {
        controller = new FilmController();
    }

    @Test
    public void shouldPassValidationTest() {
        controller.createFilm(Film.builder()
                .name("name1")
                .description("description1")
                .duration(192)
                .releaseDate(LocalDate.of(2021, 4, 5))
                .build());
        assertEquals(1, controller.allFilms().size());
    }

    @Test
    public void shouldNotPassNameValidationTest() {
        Film film = Film.builder()
                .name("")
                .description("description1")
                .duration(192)
                .releaseDate(LocalDate.of(2021, 4, 5))
                .build();
        assertThrows(ValidationException.class, () -> controller.createFilm(film));
    }

    @Test
    public void shouldNotPassDescriptionValidationTest() {
        Film film201 = Film.builder()
                .name("name1")
                .description("а".repeat(201))
                .duration(192)
                .releaseDate(LocalDate.of(2021, 4, 5))
                .build();
        Film film200 = Film.builder()
                .name("name1")
                .description("а".repeat(200))
                .duration(192)
                .releaseDate(LocalDate.of(2021, 4, 5))
                .build();
        Film film199 = Film.builder()
                .name("name1")
                .description("а".repeat(199))
                .duration(192)
                .releaseDate(LocalDate.of(2021, 4, 5))
                .build();
        controller.createFilm(film200);
        controller.createFilm(film199);
        assertThrows(ValidationException.class, () -> controller.createFilm(film201));
        assertEquals(2, controller.allFilms().size());
    }

    @Test
    public void shouldNotPassReleaseDateValidationTest() {
        Film film1 = Film.builder()
                .name("name1")
                .description("description")
                .duration(192)
                .releaseDate(LocalDate.of(1895, 12, 27))
                .build();
        assertThrows(ValidationException.class, () -> controller.createFilm(film1));
    }

    @Test
    public void shouldNotPassDurationValidationTest() {
        Film film = Film.builder()
                .name("name1")
                .description("description1")
                .duration(-1)
                .releaseDate(LocalDate.of(2025, 4, 5))
                .build();
        assertThrows(ValidationException.class, () -> controller.createFilm(film));
    }

    @Test
    public void emptyFilmShouldNotPassValidationTest() {
        Film film = Film.builder().build();
        assertThrows(ValidationException.class, () -> controller.createFilm(film));
    }

    @Test
    public void shouldUpdateFilmTest() {
        controller.createFilm(Film.builder()
                .name("name1")
                .description("description1")
                .duration(80)
                .releaseDate(LocalDate.of(2021, 4, 5))
                .build());

        controller.updateFilm(Film.builder()
                .id(1L)
                .name("newName")
                .description("newDesc")
                .duration(50)
                .releaseDate(LocalDate.of(2024, 3, 3))
                .build());
        assertTrue(controller.allFilms().toString().contains("newDesc"));
        assertTrue(controller.allFilms().toString().contains("newName"));
        assertEquals(1, controller.allFilms().size());
    }

    @Test
    public void shouldPassReleaseDateValidationTest() {
        controller.createFilm(Film.builder()
                .name("name1")
                .description("description")
                .duration(192)
                .releaseDate(LocalDate.of(1895, 12, 28))
                .build());
        assertEquals(1, controller.allFilms().size());
    }
}