package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> findAllUsers();

    User findUser(int userId);

    User createUsers(User user);

    User updateUser(User newUser);

    void validationUser(User user);

    long getNextId();
}
