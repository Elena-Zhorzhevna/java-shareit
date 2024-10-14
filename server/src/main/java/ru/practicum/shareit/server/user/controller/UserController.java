package ru.practicum.shareit.server.user.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.model.User;
import ru.practicum.shareit.server.user.service.UserService;

import java.util.Collection;

/**
 * Класс контроллера для управления пользователями в приложении ShareIt.
 */
@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {
    @Qualifier("userServiceImpl")
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Обрабатывает GET-запросы для получения всех пользователей.
     *
     * @return Коллекция всех пользователей.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<UserDto> findAll() {
        log.info("Запрос на получение всех пользователей.");
        return userService.getAllUsers();
    }

    /**
     * Обрабатывает GET-запросы для получения пользователя по идентификатору.
     *
     * @param userId Идентификатор пользователя.
     * @return Пользователь с указанным идентификатором.
     */
    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUserById(@PathVariable("userId") Long userId) {
        log.info("Запрос на получение пользователя. Идентификатор пользователя: {}", userId);
        return userService.getUserById(userId);
    }

    /**
     * Обрабатывает POST-запросы для добавления пользователя.
     *
     * @param user Пользователь, который должен быть добавлен.
     * @return Добавляемый пользователь.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@RequestBody @Valid User user) {
        log.info("Запрос на создание пользователя: \n{}", user);
        return userService.addUser(user);
    }

    /**
     * Обрабатывает PATCH-запрос на обновление существующего пользователя.
     *
     * @param userId  Идентификатор пользователя, данные которого нужно обновить.
     * @param userDto Объект DTO с новыми данными для обновления.
     * @return Обновленный пользователь в формате userDto.
     */
    @PatchMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        log.info("Обновление пользователя. Идентификатор пользователя: {}.\nНовые данные: {}", userId, userDto);
        return userService.updateUser(userId, userDto);
    }

    /**
     * Обрабатывает DELETE-запрос на удаление всех пользователей.
     */
    @DeleteMapping()
    @ResponseStatus(HttpStatus.OK)
    public void deleteAllUsers() {
        log.info("Запрос на удаление всех пользователей.");
        userService.removeAllUsers();
    }

    /**
     * Обрабатывает DELETE-запрос на удаление пользователя по его идентификатору.
     *
     * @param userId Идентификатор пользователя.
     */
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUserById(@PathVariable Long userId) {
        log.info("Получен запрос на удаление пользователя с id = {}", userId);
        userService.removeUserById(userId);
    }
}