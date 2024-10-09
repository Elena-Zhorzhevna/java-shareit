package ru.practicum.shareit.server.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Класс представляет объект, который будет возвращать сервис при запросе данных о пользователе.
 */
@NoArgsConstructor
@Data
public class UserDto {
    private Long id;
    @NotNull
    private String name;
    @Email
    @NotNull
    @NotBlank
    private String email;

    public UserDto(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}