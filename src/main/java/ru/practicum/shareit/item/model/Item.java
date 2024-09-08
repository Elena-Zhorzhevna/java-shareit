package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

/**
 * Класс представляет модель данных для объекта Item.
 */
@Data
@Builder
public class Item {
    /**
     * Уникальный идентификатор вещи.
     */
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
    @NotNull
    @NotBlank
    private Boolean available;
    /**
     * Идентификатор владелеца вещи.
     */
    private Long owner;
    /**
     * Запрос другого пользователя, с которым сожет быть связана вещь.
     */
    private String request;
}