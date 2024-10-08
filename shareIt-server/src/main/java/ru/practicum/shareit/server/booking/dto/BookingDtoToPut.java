package ru.practicum.shareit.server.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingDtoToPut {

    /**
     * Идентификатор вещи.
     */
    private Long itemId;

    /**
     * Время начала бронирования.
     */
    @FutureOrPresent
    private LocalDateTime start;

    /**
     * Время окончаиня бронирования.
     */
    @Future
    private LocalDateTime end;
}