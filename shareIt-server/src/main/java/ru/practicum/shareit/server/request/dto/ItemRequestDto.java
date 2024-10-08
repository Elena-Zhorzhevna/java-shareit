package ru.practicum.shareit.server.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.user.model.User;

import java.time.LocalDateTime;

import java.util.Collection;

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