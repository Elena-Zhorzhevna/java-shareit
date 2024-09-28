package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

/**
 * Имплементирует интерфейс UserStorage, содержит логику хранения, обновления и поиска объектов User.
 */
@Deprecated
@Slf4j
@Component("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    /**
     * Получение всех пользователей.
     *
     * @return Коллекция пользователей.
     */
    @Override
    public Collection<User> findAll() {
        log.info("Получен список всех пользователей");
        return users.values();
    }

    @Override
    public Collection<User> findAll(Collection<Long> ids) {
        List<User> rsl = new ArrayList<>(ids.size());
        for (Long id : ids) {
            var user = users.get(id);
            if (user == null) {
                continue;
            }
            rsl.add(user);
        }
        return rsl;
    }

    /**
     * Создание пользователя.
     *
     * @param user Пользователь для добавления.
     * @return Добавленный пользователь.
     */
    @Override
    public User create(User user) {
        log.info("Получен запрос на добавление пользователя: {}", user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Создан пользователь: {}", user);
        return user;
    }

    /**
     * Обновление существующего пользователя.
     *
     * @param newUser Пользователь с обновленными данными.
     * @return Обновленный пользователь.
     */
    @Override
    public User update(User newUser) {
        log.info("Получен запрос на обновление пользователя: {}", newUser);
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            log.debug("Пользователь до обновления: {}", oldUser);
            // если пользователь найден, обновляем
            oldUser.setName(newUser.getName());
            oldUser.setEmail(newUser.getEmail());
            log.info("Пользователь после обновления: {}", newUser);
            return oldUser;
        }
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    /**
     * Получение пользователя по идентификатору.
     *
     * @param userId Идентификатор пользователя.
     * @return Пользователь с заданным идентификатором.
     */
    @Override
    public User findUserById(Long userId) {
        return users.values().stream()
                .filter(u -> Objects.equals(u.getId(), userId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id %d не найден", userId)));
    }

    /**
     * Удаление пользователя по идентификатору.
     */
    @Override
    public void removeUserById(Long userId) {
        users.remove(userId);
        log.info("Удален пользователь с идентификатором {}", userId);
    }

    /**
     * Удаление всех аользователей.
     */
    @Override
    public void removeAllUsers() {
        users.clear();
        log.info("Все пользователи удалены.");
    }

    /**
     * Вспомогательный метод для генерации идентификатора нового пользователя.
     *
     * @return Новый уникальный идентификатор пользователя.
     */
    private Long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}