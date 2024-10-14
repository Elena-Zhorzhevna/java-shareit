package ru.practicum.shareit.server.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import ru.practicum.shareit.server.item.model.Item;

import java.time.LocalDateTime;

/**
 * Класс представляет объект, который будет возвращать сервис при запросе данных о комментарии.
 */
@Builder
@Data
@NoArgsConstructor
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

    public CommentDto(Long id, String text, Item item, String authorName) {
        this.id = id;
        this.text = text;
        this.item = item;
        this.authorName = authorName;
    }
}