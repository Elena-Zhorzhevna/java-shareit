package ru.practicum.shareit.server.item.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.server.user.model.User;

/**
 * Класс представляет модель данных для объекта Item.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "items")
public class Item {

    /**
     * Уникальный идентификатор вещи.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Краткое название.
     */
    @NotBlank
    @NotNull
    private String name;

    /**
     * Развернутое описание.
     */
    @NotBlank
    @NotNull
    private String description;

    /**
     * Статус о том, доступна или нет вещь для аренды.
     */
    private Boolean available;

    /**
     * Идентификатор владелеца вещи.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    /**
     * Идентификатор запроса другого пользователя, с которым может быть связана вещь.
     */
    @Column(name = "request_id")
    private Long requestId;
}