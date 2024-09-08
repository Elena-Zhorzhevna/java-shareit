package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Класс представляет объект, который будет возвращать сервис при запросе данных о вещи.
 */
@Data
public class ItemDto {
    Long id;
    @NotNull
    @NotBlank
    String name;
    @NotNull
    String description;
    @NotNull
    Boolean available;
    String request;
}