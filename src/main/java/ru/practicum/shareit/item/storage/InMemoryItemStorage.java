package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.*;

/**
 * Имплементирует интерфейс ItemStorage, содержит логику хранения, обновления и поиска объектов Item.
 */
@Slf4j
@Component("inMemoryItemStorage")
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
    private final UserStorage userStorage;

    @Autowired
    public InMemoryItemStorage(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    /**
     * Получение всех вещей.
     *
     * @return Коллекция вещей.
     */
    @Override
    public Collection<Item> getAll() {
        log.info("Запрос на получение всех вещей.");
        return items.values();
    }

    /**
     * Получение всех вещей по идентификатору владельца.
     *
     * @param id Идентификатор владельца.
     * @return Список вещей пользователя - владельца вещей.
     */
    @Override
    public List<Item> findItemByOwnerId(Long id) {
        log.debug("Получен список вещей пользователя с id {}", id);
        return new ArrayList<>(items.values().stream()
                .filter(item -> Objects.equals(item.getOwner(), id)).toList());
    }

    /**
     * Получение вещи по идентификатору.
     *
     * @param id Идентификатор вещи
     * @return Вещь с
     */
    @Override
    public Item findItemById(Long id) {
        return items.values()
                .stream()
                .filter(item -> Objects.equals(item.getId(), id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(String.format("Фильм с id %d не найден", id)));
    }

    /**
     * Добавление вещи.
     *
     * @param item Добавляемая вещь.
     * @return Добавленная вещь.
     */
    @Override
    public Item create(Long userId, Item item) {
        log.info("Получен запрос на добавление вещи: {}", item);
        item.setId(getNextId());
        items.put(item.getId(), item);
        log.info("Добавлена вещь: {}", item);
        return item;
    }

    /**
     * Обновление существующей вещи.
     *
     * @return Обновленная вещь.
     */
    @Override
    public Item update(Long userId, Long itemId, Item newItem) {
        Item item = items.get(itemId);
        isOwnerCheck(userId, item);

        if (newItem.getName() != null) {
            item.setName(newItem.getName());
        }
        if (newItem.getDescription() != null) {
            item.setDescription(newItem.getDescription());
        }
        if (newItem.getAvailable() != null) {
            item.setAvailable(newItem.getAvailable());
        }
        items.put(itemId, item);
        return item;
    }

    /**
     * Удаление вещи по ее идентификатору.
     */
    @Override
    public void removeItemById(Long itemId) {
        items.remove(itemId);
        log.debug("Удалена вещь с id {}", itemId);
    }

    /**
     * Удаление всех вещей, принадлежащих определенному владельцу.
     */
    @Override
    public void removeItemByOwnerId(Long ownerId) {
        findItemByOwnerId(ownerId).stream().filter(item -> Objects.equals(item.getOwner(), ownerId))
                .forEach(item -> removeItemById(item.getId()));
    }

    /**
     * Удаление всех вещей.
     */
    @Override
    public void removeAllItems() {
        items.clear();
        log.debug("Удалены все вещи");
    }

    /**
     * Метод проверяет, является ли пользователь владельцем вещи.
     *
     * @param userId Идентификатор пользователя.
     * @param item   Вещь, владелец которой проверяется.
     */
    private void isOwnerCheck(long userId, Item item) {
        userStorage.findUserById(userId);
        log.debug("Проверка, является ли пользователь владельцем вещи. id владельца: {}, id вещи: {}",
                userId, item.getId());

        if (userId != item.getOwner()) {
            throw new NotFoundException("Пользователь не является владельцем.");
        }
    }

    /**
     * Вспомогательный метод для генерации идентификатора новой вещи.
     *
     * @return Сгенерированный уникальный идентификатор вещи.
     */
    private Long getNextId() {
        long currentMaxId = items.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}