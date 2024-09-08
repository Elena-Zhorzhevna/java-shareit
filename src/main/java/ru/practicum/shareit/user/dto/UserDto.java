package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Класс представляет объект, который будет возвращать сервис при запросе данных о пользователе.
 */
@Data
public class UserDto {
    Long id;
    @NotNull
    String name;
    @Email
    @NotNull
    @NotBlank
    String email;
}
