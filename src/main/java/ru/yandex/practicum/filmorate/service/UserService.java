package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public void createUser(User user) {
        userStorage.createUsers(user);
    }

    public void updateUser(User user) {
        if (user.getId() == null) {
            log.error("нет айди");
            throw new ValidationException("Id должен быть указан");
        }
        if (userStorage.findUser(Math.toIntExact(user.getId())) == null) {
            log.error("ошибка с id {}", user.getId());
            throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден");
        }
        userStorage.updateUser(user);
    }

    public User findUser(int id) {
        return userStorage.findUser(id);
    }

    public List<User> getUsers() {
        return userStorage.findAllUsers();
    }

    public void addFriend(int firstId, int secondId) {
        log.info("Пользователь {} пытается добавить в друзья пользователя {}", firstId, secondId);
        try {
            if ((userStorage.findUser(firstId) == null) || (userStorage.findUser(secondId) == null)) {
                log.error("ошибка с id  {}", firstId);
                throw new NotFoundException("Таких id найдено");
            }
            userStorage.addFriends((long) firstId, (long) secondId);
            log.info("Добавление в друзья удалось");
        } catch (NotFoundException e) {
            log.warn("Не удалось добавить пользователей в друзья: {}", e.getMessage());
            throw e;
        }
    }

    public void deleteFriend(int firstId, int secondId) {
        log.info("Пользователь {} пытается удалить пользователя {} из друзей", firstId, secondId);
        if ((userStorage.findUser(firstId) == null) || (userStorage.findUser(secondId) == null)) {
            log.error("ошибка с id  {}", firstId);
            throw new NotFoundException("Таких id найдено");
        }
        userStorage.removeFriends((long) firstId, (long) secondId);
        log.info("Удаление из друзей удалось");
    }

    public List<User> getFriends(int id) {
        log.info("Попытка получить друзей пользователя {}", id);
        if (userStorage.findUser(id) == null) {
            log.error("ошибка с id  {}", id);
            throw new NotFoundException("Такого id найдено");
        }
        return userStorage.getFriends((long) id);
    }

    public List<User> getMutualFriends(int firstId, int secondId) {
        log.info("Попытка получить общих друзей пользователей {} и {}", firstId, secondId);
        try {
            User firstUser = userStorage.findUser(firstId);
            User secondUser = userStorage.findUser(secondId);
            log.info("Получение общих друзей");
            return firstUser.getFriends().stream()
                    .filter(secondUser.getFriends()::contains).map(userStorage::findUser)
                    .collect(Collectors.toList());
        } catch (NotFoundException e) {
            log.warn("Не удалось получить общих друзей: {}", e.getMessage());
            throw e;
        }
    }
}
