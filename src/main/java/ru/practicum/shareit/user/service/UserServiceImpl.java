package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Сервисный класс, который обрабатывает операции и взаимодействия, связанные с пользователями.
 * Во всех случаях возвращает объекты UserDto.
 */
@Slf4j
@Service(("userServiceImpl"))
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    /**
     * Получение всех пользователей.
     *
     * @return Коллекция пользователей.
     */
    @Override
    public Collection<UserDto> getAllUsers() {
        return userStorage.findAll()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    /**
     * Получение пользователя по идентификатору.
     *
     * @param id Идентификатор пользователя.
     * @return Пользователь с заданным идентификатором.
     */

    @Override
    public UserDto getUserById(long id) {
        log.info("Попытка получить пользователя с id={}", id);
        User user = userStorage.findUserById(id);
        if (userStorage.findUserById(id) == null) {
            throw new NotFoundException(String.format("Пользователь с id=%d не найден", id));
        }
        log.info("Пользователь с id = {} получен.", id);
        return UserMapper.mapToUserDto(user);
    }

    /**
     * Добавление пользователя.
     *
     * @param user Добавляемый пользователь.
     * @return Добавленный пользователь.
     */
    @Override
    public UserDto addUser(User user) {
        if (user.getEmail() == null) {
            throw new ValidationException("Электронная почта не должна быть пустой.");
        }
        emailValidation(user.getEmail());
        return UserMapper.mapToUserDto(userStorage.create(user));
    }

    /**
     * Обновление существующего пользователя.
     *
     * @param newUserDto Пользователь с обновленными данными.
     * @return Обновленный пользователь.
     */
    @Override
    public UserDto updateUser(Long userId, UserDto newUserDto) {
        User userToUpdate = userStorage.findUserById(userId);
        emailValidation(newUserDto.getEmail());
        log.info("Пользователь до изменения: \n{}", userToUpdate);
        Optional.ofNullable(newUserDto.getName()).ifPresent(userToUpdate::setName);
        Optional.ofNullable(newUserDto.getEmail()).ifPresent(userToUpdate::setEmail);
        log.info("Обновлённый пользователь: \n{}", userToUpdate);
        UserDto newDto = UserMapper.mapToUserDto(userStorage.update(userToUpdate));
        log.info("Обновлённый пользователь в формате DTO: \n{}", newDto);
        return newDto;
    }

    /**
     * Удаление всех пользователей.
     */
    @Override
    public void removeAllUsers() {
        userStorage.removeAllUsers();
    }

    /**
     * Удаление пользователя по id.
     */
    @Override
    public void removeUserById(Long userId) {
        userStorage.removeUserById(userId);
    }

    /**
     * Метод проверяет уникальность электронной почты пользователя.
     *
     * @param email Электронная почта пользователя.
     */
    private void emailValidation(String email) {
        if (!userStorage.findAll().stream().filter(oldUser -> oldUser.getEmail().equals(email)).toList().isEmpty()) {
            log.warn("Ошибка. Пользователь с email: {} уже существует.", email);
            throw new ConflictException("Пользователь с email: " + email + " уже существует.");
        }
    }
}