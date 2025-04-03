package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> findAllUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody User user) {
        log.info("Успешное создание нового пользователя: {}", user);
        userService.createUser(user);
        return ResponseEntity.ok(user);
    }

    @PutMapping
    public ResponseEntity<?> updateUser(@Valid @RequestBody User user) {
        log.info("Успешное обновление пользователя: {}", user);
        userService.updateUser(user);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findUser(@PathVariable int id) {
        log.info("Получение пользователя по id = {}", id);
        return ResponseEntity.ok(userService.findUser(id));
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<?> addFriend(@PathVariable("id") int userId, @PathVariable int friendId) {
        log.info("Добавление в друзья пользователя с id {}", friendId);
        userService.addFriend(userId, friendId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<?> deleteFriend(@PathVariable("id") int userId, @PathVariable int friendId) {
        log.info("Удаление из друзей из друзей пользователя с id {}", friendId);
        userService.deleteFriend(userId, friendId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/{id}/friends")
    public ResponseEntity<List<User>> getFriends(@PathVariable int id) {
        log.info("Получение всех друзей пользователя с id = {}", id);
        return ResponseEntity.ok(userService.getFriends(id));
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public ResponseEntity<Collection<User>> getFriends(@PathVariable int id, @PathVariable int otherId) {
        log.info("Получение общих друзей пользователей с id {} и {}", id, otherId);
        return ResponseEntity.ok(userService.getMutualFriends(id, otherId));
    }

}
