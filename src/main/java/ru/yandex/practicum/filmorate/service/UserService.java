package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void createUser(User user) {
        userStorage.createUsers(user);
    }

    public void updateUser(User user) {
        userStorage.updateUser(user);
    }

    public User findUser(int id) {
        return userStorage.findUser(id);
    }

    public Collection<User> getUsers() {
        return userStorage.findAllUsers();
    }

    public void addFriend(int firstId, int secondId) {
        log.info("Пользователь {} пытается добавить в друзья пользователя {}", firstId, secondId);
        try {
            User firstUser = userStorage.findUser(firstId);
            User secondUser = userStorage.findUser(secondId);
            firstUser.addFriend(secondId);
            secondUser.addFriend(firstId);
            log.info("Добавление в друзья удалось");
        } catch (NotFoundException e) {
            log.warn("Не удалось добавить пользователей в друзья: {}", e.getMessage());
            throw e;
        }
    }

    public void deleteFriend(int firstId, int secondId) {
        log.info("Пользователь {} пытается удалить пользователя {} из друзей", firstId, secondId);
        try {
            User firstUser = userStorage.findUser(firstId);
            User secondUser = userStorage.findUser(secondId);
            firstUser.deleteFriend(secondId);
            secondUser.deleteFriend(firstId);
            log.info("Удаление из друзей удалось");
        } catch (NotFoundException e) {
            log.warn("Не удалось удалить пользователя из друзей: {}", e.getMessage());
            throw e;
        }
    }

    public Collection<User> getFriends(int id) {
        log.info("Попытка получить друзей пользователя {}", id);
        try {
            User user = userStorage.findUser(id);
            log.info("Получение друзей удалось");
            return user.getFriends().stream()
                    .map(userStorage::findUser)
                    .collect(Collectors.toList());
        } catch (NotFoundException e) {
            log.warn("Не удалось получить друзей пользователя: {}", e.getMessage());
            throw e;
        }
    }

    public Collection<User> getMutualFriends(int firstId, int secondId) {
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
