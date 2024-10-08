package ru.practicum.shareit.server.booking.service;

import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.booking.dto.BookingDtoToPut;

import java.util.List;

public interface BookingService {

    /**
     * Создание бронирования.
     */
    BookingDto create(BookingDtoToPut bookingDtoToPut, Long bookerId);

    /**
     * Обновление бронирования.
     */
    BookingDto update(Long bookingId, Long userId, Boolean approved);

    /**
     * Получение бронирования по идентификатору бронирования и идентификатору владельца.
     */
    BookingDto getBookingById(Long bookingId, Long userId);

    /**
     * Полоучение бронирований пользователя с указанным параметром state.
     */
    List<BookingDto> getBookings(String state, Long userId);

    /**
     * Получение последнего бронирования.
     */
    BookingDto getLastBooking(Long itemId);

    /**
     * Получение следующего бронирования.
     */
    BookingDto getNextBooking(Long itemId);

    /**
     * Получение бронирований для всех вещей владельца с указанным параметром state.
     */
    List<BookingDto> getBookingsOfOwnerItems(Long ownerId, String state);

    List<BookingDto> getBookingsByUserIdWithState(String state, Long userId);
}