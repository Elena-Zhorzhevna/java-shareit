package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Data;

/**
 * Класс представляет модель пользователя
 */
@Data
@Builder
public class User {
    /**
     * Уникальный идентификатор пользователя.
     */
    private Long id;
    /**
     * Имя или логин пользователя.
     */
    private String name;
    /**
     * Адрес электронной почты пользователя /два пользователя не могут
     * иметь одинаковый адрес электронной почты/.
     */
    @Email
    private String email;
}
