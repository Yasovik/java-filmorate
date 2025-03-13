package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> findAllUsers() {
        log.info("Запрошен список всех пользователей, в списке {} пользователей", users.size());
        return users.values();
    }

    @Override
    public User findUser(int userId) {
        if (users.containsKey((long) userId)) {
            log.info("Пользователь успешно получен");
            return users.get((long) userId);
        } else {
            log.warn("Попытка получить несуществующего пользователя");
            throw new NotFoundException("Пользователь не найден");
        }
    }

    @Override
    public User createUsers(User user) {
        validationUser(user);
        user.setLogin(user.getLogin().trim().replace(" ", "_"));
        user.setId(getNextId());
        putUserFriends(user);
        users.put(user.getId(), user);
        log.info("Создан пользователь с именем: {}", user.getName());
        return user;
    }

    @Override
    public User updateUser(User newUser) {
        if (!users.containsKey(newUser.getId())) {
            throw new NotFoundException("Пользователя с id " + newUser.getId() + " не существует");
        }
        validationUser(newUser);
        newUser.setLogin(newUser.getLogin().trim().replace(" ", "_"));
        putUserFriends(newUser);
        users.put(newUser.getId(), newUser);
        log.info("Пользователь с именем: {} обновлен", newUser.getName());
        return newUser;
    }

    @Override
    public void validationUser(User user) {
        try {
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
        } catch (ValidationException exception) {
            log.error("Ошибка при создании пользователя: {}", exception.getMessage());
            throw exception;
        }
    }

    private void putUserFriends(User user) {
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
            log.info("Друзья пользователя заполнены, как пустой HashSet");
        }
    }

    @Override
    public long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
