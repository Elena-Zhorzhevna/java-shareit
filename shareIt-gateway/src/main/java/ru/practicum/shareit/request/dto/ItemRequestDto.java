package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.validation.annotation.Validated;

@Data
@Builder
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {
    @NotBlank
    private String description;
}