package ru.practicum.shareit.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Специальный класс с описанием ошибки, объект которого контроллер вернет объекту в случае возникновения проблем.
 */
@Getter
@AllArgsConstructor
public class ErrorResponse {
    /**
     * Название ошибки.
     */
    private String error;
    /**
     * Подробное описание ошибки.
     */
    private String description;
}