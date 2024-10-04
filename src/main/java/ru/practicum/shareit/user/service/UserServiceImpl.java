package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Сервисный класс, который обрабатывает операции и взаимодействия, связанные с пользователями.
 * Во всех случаях возвращает объекты UserDto.
 */
@Slf4j
@Service(("userServiceImpl"))
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Получение всех пользователей.
     *
     * @return Коллекция пользователей.
     */
    @Override
    @Transactional(readOnly = true)
    public Collection<UserDto> getAllUsers() {
        return userRepository.findAll()
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
    public UserDto getUserById(Long id) {
        log.info("Попытка получить пользователя с id={}", id);
        return UserMapper.mapToUserDto(userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден!")));
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
        return UserMapper.mapToUserDto(userRepository.save(user));
    }

    /**
     * Обновление существующего пользователя.
     *
     * @param newUserDto Пользователь с обновленными данными.
     * @return Обновленный пользователь.
     */
    @Override
    public UserDto updateUser(Long userId, UserDto newUserDto) {
        if (newUserDto.getId() == null) {
            newUserDto.setId(userId);
        }
        User userToUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден!"));
        emailValidation(newUserDto.getEmail());

        if (newUserDto.getName() != null) {
            userToUpdate.setName(newUserDto.getName());
        }

        if (newUserDto.getEmail() != null) {
            userToUpdate.setEmail(newUserDto.getEmail());
        }
        log.info("Обновлённый пользователь: \n{}", userToUpdate);
        UserDto newDto = UserMapper.mapToUserDto(userRepository.save(userToUpdate));
        log.info("Обновлённый пользователь в формате DTO: \n{}", newDto);
        return newDto;
    }

    /**
     * Удаление всех пользователей.
     */
    @Override
    public void removeAllUsers() {
        userRepository.deleteAll();
    }

    /**
     * Удаление пользователя по id.
     */
    @Override
    public void removeUserById(Long userId) {
        getUserById(userId);
        userRepository.deleteById(userId);
    }

    /**
     * Метод проверяет уникальность электронной почты пользователя.
     *
     * @param email Электронная почта пользователя.
     */
    private void emailValidation(String email) {
        if (!userRepository.findAll().stream().filter(oldUser -> oldUser.getEmail().equals(email)).toList().isEmpty()) {
            log.warn("Пользователь с email: {} уже существует.", email);
            throw new ConflictException("Пользователь с email: " + email + " уже существует.");
        }
    }
}