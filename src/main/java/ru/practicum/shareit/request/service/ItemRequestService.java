package ru.practicum.shareit.request.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

/**
 * Интерфейс, в котором определены методы добавления, удаления и модификации объектов ItemRequest.
 */
public interface ItemRequestService {
    /**
     * Получение всех запросов пользователя с указанным идентификатором.
     */
    List<ItemRequestDto> getAllItemRequestsByUserId(Long userId);

    /**
     * Получение всех запросов других пользователей.
     */
    List<ItemRequestDto> getAllRequests(Long userId, Integer pageNum, Integer pageSize);

    /**
     * Добавление нового запроса.
     */
    ItemRequestDto addItemRequest(Long userId, ItemRequestDto itemRequestDto);

    /**
     * Получение запроса по его идентификатору.
     */
    ItemRequestDto getItemRequestById(Long itemRequestId, Long userId);

    /**
     * Добавление вещи к сущности запроса.
     */
    void addItemToRequest(ItemDto itemDto);
}