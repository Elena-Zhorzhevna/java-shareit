package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * Класс представляет модель данных для объекта Comment.
 */
@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {
    /**
     * Уникальный идентификатор комментария
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Содержимое комментария.
     */
    @NotBlank
    @NotEmpty
    private String text;

    /**
     * Вещь, к которой относится комментарий.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    /**
     * Автор комментария.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    /**
     * Дата создания комментария.
     */
    @Column(name = "created")
    private LocalDateTime created;
}