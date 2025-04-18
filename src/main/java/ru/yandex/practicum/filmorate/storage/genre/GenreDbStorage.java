package ru.yandex.practicum.filmorate.storage.genre;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@Component
@Primary
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> getAll() {
        return jdbcTemplate.query("SELECT * FROM genres", new DataClassRowMapper<>(Genre.class));
    }

    @Override
    public Genre getById(Long id) {
        try {
            return jdbcTemplate.queryForObject("SELECT id, name FROM genres WHERE id = ?", new DataClassRowMapper<>(Genre.class), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Жанр с id = " + id + " не найден");
        }
    }

    @Override
    public List<Genre> getGenresOfFilm(Long id) {
        try {
            return jdbcTemplate.query("SELECT * FROM genres WHERE id IN (SELECT genre_id FROM films_genre WHERE film_id = ?);", new DataClassRowMapper<>(Genre.class), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public boolean checkGenresExists(List<Genre> genres) {
        if (genres.isEmpty()) {
            return true;
        }
        List<Long> genreIds = genres.stream()
                .map(Genre::getId)
                .toList();
        String sql = "SELECT id FROM genres WHERE id IN (" +
                String.join(",", Collections.nCopies(genreIds.size(), "?")) + ")";
        List<Long> existingIds = jdbcTemplate.queryForList(sql, Long.class, genreIds.toArray());
        for (Genre genre : genres) {
            if (!existingIds.contains(genre.getId())) {
                throw new NotFoundException("Жанр с id = " + genre.getId() + " отсутствует");
            }
        }
        return true;
    }
}