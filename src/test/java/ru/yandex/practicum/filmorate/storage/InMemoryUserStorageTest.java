package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class InMemoryUserStorageTest {
    private final InMemoryUserStorage controller = new InMemoryUserStorage();

    @Test
    public void shouldPassValidationTest() {
        controller.createUsers(User.builder()
                .login("login1")
                .name("name1")
                .email("mail1@mail.ru")
                .birthday(LocalDate.of(1979, 3, 25))
                .build());
        assertEquals(1, controller.findAllUsers().size());
    }

    @Test
    public void shouldNotPassEmailValidationTest() {
        User user1 = User.builder()
                .login("login1")
                .name("name1")
                .email("mail1.ru")
                .birthday(LocalDate.of(1979, 3, 25))
                .build();
        User user2 = User.builder()
                .login("login2")
                .name("name2")
                .email("")
                .birthday(LocalDate.of(2003, 6, 8))
                .build();
        assertThrows(ValidationException.class, () -> controller.createUsers(user1));
        assertThrows(ValidationException.class, () -> controller.createUsers(user2));
    }

    @Test
    public void shouldNotPassLoginValidationTest() {
        User user1 = User.builder()
                .login("")
                .name("name1")
                .email("mail1@mail.ru")
                .birthday(LocalDate.of(2003, 6, 15))
                .build();
        User user2 = User.builder()
                .login(" login2")
                .name("name2")
                .email("mail2@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();
        assertThrows(ValidationException.class, () -> controller.createUsers(user1));
        assertThrows(ValidationException.class, () -> controller.createUsers(user2));
    }

    @Test
    public void shouldNotPassBirthdayValidationTest() {
        User user = User.builder()
                .login("login1")
                .name("name1")
                .email("mail1@mail.ru")
                .birthday(LocalDate.now().plusDays(1))
                .build();
        assertThrows(ValidationException.class, () -> controller.createUsers(user));
    }

    @Test
    public void emptyUserShouldNotPassValidationTest() {
        User user = User.builder().build();
        assertThrows(NullPointerException.class, () -> controller.createUsers(user));
    }

    @Test
    public void shouldUpdateUser() {
        controller.createUsers(User.builder()
                .login("login1")
                .name("name1")
                .email("mail1@mail.ru")
                .birthday(LocalDate.of(2003, 6, 8))
                .build());
        controller.updateUser(User.builder()
                .id(1L)
                .login("login2")
                .name("name2")
                .email("mail2@mail.ru")
                .birthday(LocalDate.of(2003, 6, 8))
                .build());
        assertEquals(1, controller.findAllUsers().size());
    }

    @Test
    public void shouldCreateUserWithEmptyName() {
        User user = User.builder()
                .login("login1")
                .email("mail1@mail.ru")
                .birthday(LocalDate.of(2003, 6, 8))
                .build();
        controller.createUsers(user);
        assertEquals("login1", user.getName());
    }
}