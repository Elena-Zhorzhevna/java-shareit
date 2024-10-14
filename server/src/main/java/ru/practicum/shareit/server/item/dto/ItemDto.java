package ru.practicum.shareit.server.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.server.booking.dto.BookingDtoShort;
import ru.practicum.shareit.server.user.model.User;

import java.util.List;

/**
 * Класс представляет объект, который будет возвращать сервис при запросе данных о вещи.
 */
@RequiredArgsConstructor
@AllArgsConstructor
@Data
public class ItemDto {
    /**
     * Идентификатор вещи.
     */
    private Long id;

    /**
     * Название вещи.
     */
    private String name;
    /**
     * Описание вещи.
     */
    private String description;
    /**
     * Владелец вещи.
     */
    @JsonIgnore
    private User owner;

    /**
     * Статус о том, доступна или нет вещь для аренды.
     */
    private Boolean available;

    /**
     * Идентификатор запроса другого пользователя, с которым может быть связана вещь.
     */
    private Long requestId;

    /**
     * Данные о последнем бронировании вещи.
     */
    private BookingDtoShort lastBooking;

    /**
     * Данные о следующем бронировании вещи.
     */
    private BookingDtoShort nextBooking;

    /**
     * Список отзывов вещи.
     */
    private List<CommentDto> comments;

    public ItemDto(Long id, String name, String description, Long requestId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.requestId = requestId;
    }

    public ItemDto(Long id, String name, String description, Boolean available, Long requestId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.requestId = requestId;
    }

    public ItemDto(Long id, String name, String description, User owner, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.available = available;
    }
}