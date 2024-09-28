package ru.practicum.shareit.booking.model;

/**
 * Статус бронирования вещи.
 * Может иметь значения:
 * WAITING - новое бронирование, ожидает одобрения,
 * APPROVED - бронирование подтверждено владельцем,
 * REJECTED - бронирование отклонено владельцем,
 * CANCELED - бронирование отменено создателем.
 */
public enum Status {
    WAITING,
    APPROVED,
    REJECTED,
    CANCELED
}