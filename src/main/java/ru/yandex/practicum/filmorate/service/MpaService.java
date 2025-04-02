package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaStorage mpaStorage;

    public List<Mpa> getAll() {
        return mpaStorage.getAll();
    }


    public Mpa getById(Long id) {
        if (mpaStorage.getById(id) == null) {
            throw new NotFoundException("Рейтинга с таким id = " + id + " нет");
        } else {
            return mpaStorage.getById(id);
        }
    }
}