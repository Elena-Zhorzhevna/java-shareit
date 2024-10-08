package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserDto userDto) {
        log.info("Запрос на создание пользователя.");
        final ResponseEntity<Object> user = userClient.create(userDto);
        log.info("Новый пользователь создан.");
        return user;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable(value = "userId") Long userId) {
        log.info("Получение пользователя по id " + userId);
        return userClient.getUserById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("Запрос на получение всех пользователей.");
        final ResponseEntity<Object> users = userClient.getAllUsers();
        log.info("Все пользователи получены.");
        return users;
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@Valid @RequestBody UserDto userDto,
                                         @PathVariable(value = "userId") Long userId) {
        log.info("Запрос на обновление пользователя с id = {}", userId);
        final ResponseEntity<Object> user = userClient.update(userId, userDto);
        log.info("Обновлен пользователь с id = {}", userId);
        return user;
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUserById(@PathVariable(value = "userId") Long userId) {
        log.info("Запрос на удаление пользователя с id = {}", userId);
        final ResponseEntity<Object> user = userClient.delete(userId);
        log.info("Удален пользователь с id = {}", userId);
        return user;
    }
}
