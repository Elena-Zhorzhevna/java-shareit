package ru.practicum.shareit.server.booking.dto;

import lombok.*;
import ru.practicum.shareit.server.booking.model.Status;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.user.dto.UserDto;

import java.time.LocalDateTime;

/**
 * Класс представляет объект, который будет возвращать сервис при запросе данных о бронировании.
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {

    /**
     * Идентификатор бронирования.
     */
    private Long id;

    /**
     * Время начала бронирования.
     */
    private LocalDateTime start;

    /**
     * Время окончания бронирования.
     */
    private LocalDateTime end;

    /**
     * Забронированная вещь в формате Дто.
     */
    private ItemDto item;

    /**
     * Арендатор вещи.
     */
    private UserDto booker;

    /**
     * Статус бронирования.
     */
    private Status status;
}