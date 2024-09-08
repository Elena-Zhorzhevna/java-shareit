package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

/**
 * Интерфейс, в котором определены методы добавления, удаления и модификации объектов User.
 */
public interface UserService {
    /**
     * Получение всех пользователей.
     */
    Collection<UserDto> getAllUsers();

    /**
     * Получение пользователя по айди.
     */
    UserDto getUserById(long userId);

    /**
     * Добавление пользователя.
     */
    UserDto addUser(User user);

    /**
     * Обновление пользователя.
     */
    UserDto updateUser(Long userId, UserDto newUserDto);

    /**
     * Удаление всех пользователей.
     */
    void removeAllUsers();

    /**
     * Удаление пользователя по идентификатору.
     */
    void removeUserById(Long userId);
}