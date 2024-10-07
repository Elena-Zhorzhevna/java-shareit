package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

@Data
@Builder
public class ItemNameDto {
    private Long id;
    private String name;
    private User owner;
    private Long requestId;
}
