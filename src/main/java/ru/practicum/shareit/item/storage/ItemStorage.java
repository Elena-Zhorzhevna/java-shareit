package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

/**
 * Интерфейс, в котором определены методы добавления, удаления и модификации объектов Item.
 */
@Deprecated
public interface ItemStorage {

    /**
     * Получение всех вещей.
     */
    Collection<Item> getAll();

    /**
     * Получение всех вещей по идентификатору владельца.
     */
    List<Item> findItemByOwnerId(Long id);

    /**
     * Получение вещи по идентификатору.
     */
    Item findItemById(Long id);

    /**
     * Добавление вещи.
     */
    Item create(Long userId, Item item);

    /**
     * Обновление вещи;
     */
    Item update(Long userId, Long itemId, Item newItem);

    /**
     * Удаление вещи по ее идентификатору.
     */
    void removeItemById(Long itemId);

    /**
     * Удаление всех вещей, принадлежащих определенному владельцу.
     */
    void removeItemByOwnerId(Long ownerId);

    /**
     * Удаление всех вещей.
     */
    void removeAllItems();
}