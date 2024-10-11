package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDto {
    private long id;         // ID бронирования
    private long itemId;    // ID предмета
    private LocalDateTime start; // Время начала
    private LocalDateTime end;   // Время окончания
    private boolean approved; // Статус одобрения
}