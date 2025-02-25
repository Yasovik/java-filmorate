package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAllUsers() {
        log.info("Запрошен список всех пользователей, в списке {} пользователей", users.size());
        return users.values();
    }

    @PostMapping
    public User createUsers(@Valid @RequestBody User user) {
        try {
            validationUser(user);
            user.setLogin(user.getLogin().trim().replace(" ", "_"));
            user.setId(getNextId());
            users.put(user.getId(), user);
            log.info("Создан пользователь с именем: {}", user.getName());
            return user;
        } catch (ValidationException exception) {
            log.error("Ошибка при создании пользователя: {}", exception.getMessage());
            throw exception;
        }
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User newUser) {
        try {
            if (!users.containsKey(newUser.getId())) {
                throw new ValidationException("Пользователя с таким ид не существует");
            }
            validationUser(newUser);
            newUser.setLogin(newUser.getLogin().trim().replace(" ", "_"));
            users.put(newUser.getId(), newUser);
            log.info("Пользователь с именем: {} обновлен", newUser.getName());
            return newUser;
        } catch (ValidationException exception) {
            log.error("Ошибка при обновлении пользователя: {}", exception.getMessage());
            throw exception;
        }
    }

    public void validationUser(User user) {
        if (user.getName() == null) {
            log.info("При создании пользователя не указано имя, используем логин {}", user.getLogin());
            user.setName(user.getLogin().trim().replace(" ", "_"));
        }
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Почта указана некорректно");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым или содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть больше текущей даты");
        }
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
