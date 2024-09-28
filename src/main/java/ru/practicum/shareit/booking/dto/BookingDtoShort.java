package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;

@Getter
@Setter
public class BookingDtoShort {
    /**
     * Идентификатор бронирования.
     */
    private Long id;

    /**
     * Идентификатор забронированнной вещи.
     */
    private Long itemId;

    /**
     * Время начала бронирования.
     */
    private LocalDateTime start;

    /**
     * Время окончания бронирования.
     */
    private LocalDateTime end;

    /**
     * Сатус бронирования.
     */
    private Status status;
}