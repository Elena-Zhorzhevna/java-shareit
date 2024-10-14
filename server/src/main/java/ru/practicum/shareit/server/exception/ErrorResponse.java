package ru.practicum.shareit.server.exception;

import lombok.Getter;

/**
 * Специальный класс с описанием ошибки, объект которого контроллер вернет объекту в случае возникновения проблем.
 */
@Getter
public class ErrorResponse {
    /**
     * Название ошибки.
     */
    private String error;
    /**
     * Подробное описание ошибки.
     */
    private String description;

    public ErrorResponse(String error, String description) {
        this.error = error;
        this.description = description;
    }
}