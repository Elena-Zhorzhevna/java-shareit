package ru.practicum.shareit.server.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
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