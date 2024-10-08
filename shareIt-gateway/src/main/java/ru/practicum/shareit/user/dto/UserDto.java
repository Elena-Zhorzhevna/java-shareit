package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserDto {
    //private Long id;
    @NotNull
    private String name;
    @Email
    @NotNull
    @NotBlank
    private String email;
}