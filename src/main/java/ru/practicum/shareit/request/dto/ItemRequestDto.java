package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import java.util.Collection;
import java.util.List;

/**
 * Класс представляет объект, который будет возвращать сервис при запросе данных о
 * запросах вещей пользователями.
 */
@Data
@Builder
public class ItemRequestDto {
    /**
     * Идентификатор запроса вещи.
     */
    private Long id;
    /**
     * Содержание запроса вещи.
     */
    private String description;
    /**
     * Идентификатор пользователя, создавшего запрос на вещь.
     */
    private User requester;
    /**
     * Дата и время создания запроса.
     */
    private LocalDateTime created;
    /**
     * Список ответов в формате: id вещи, название, id владельца.
     */
    private Collection<Item> items;
}