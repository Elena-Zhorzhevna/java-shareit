package ru.practicum.shareit.server.booking.model;

/**
 * Статус бронирования вещи.
 */
public enum Status {
    /**
     * Новое бронирование, ожидает одобрения.
     */
    WAITING,
    /**
     * Бронирование подтверждено владельцем.
     */
    APPROVED,
    /**
     * Бронирование отклонено владельцем.
     */
    REJECTED,
    /**
     * Бронирование отменено создателем.
     */
    CANCELED
}