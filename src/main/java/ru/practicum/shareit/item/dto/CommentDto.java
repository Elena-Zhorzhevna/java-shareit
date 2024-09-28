package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;

/**
 * Класс представляет объект, который будет возвращать сервис при запросе данных о комментарии.
 */
@Builder
@Data
@AllArgsConstructor
public class CommentDto {
    /**
     * Уникальный идентификатор комментария.
     */
    private Long id;

    /**
     * Содержимое комментария.
     */

    private String text;

    /**
     * Вещь, к которой относится комментарий.
     */
    @JsonIgnore
    private Item item;

    /**
     * Имя автора комментария.
     */
    private String authorName;

    /**
     * Дата создания комментария.
     */
    private LocalDateTime created;
}