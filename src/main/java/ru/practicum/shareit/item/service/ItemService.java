package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс, в котором определены методы добавления, удаления и модификации объектов Item.
 */
public interface ItemService {

    /**
     * Получение всех вещей.
     */
    Collection<ItemDto> getAll();

    /**
     * Получение всех вещей владельца.
     */
    List<ItemDto> getAllItemsByUserId(Long userId);

    /**
     * Получение вещи по идентификатору.
     */
    Optional<ItemDto> getItemById(Long id);

    /**
     * Поиск вещей.
     */
    Collection<ItemDto> searchItemsByText(String text);

    /**
     * Добавление вещи.
     */
    ItemDto addItem(Long userId, ItemDto itemDto);

    /**
     * Обновление вещи.
     */
    ItemDto updateItem(Long userId, Long itemId, Item newItem);

    /**
     * Удаление всех вещей определенного пользователя.
     */
    void removeAllItemsByOwnerId(Long userId);

    /**
     * Удаление вещи по ее идентификатору.
     */
    void removeItemById(Long userId, Long itemId);
}