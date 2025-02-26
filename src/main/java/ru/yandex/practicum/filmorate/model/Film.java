package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * Film.
 */
@Data
@Builder
public class Film {
    private Long id;
    @NotBlank
    private String name;
    private String description;
    LocalDate releaseDate;
    private int duration;
}
