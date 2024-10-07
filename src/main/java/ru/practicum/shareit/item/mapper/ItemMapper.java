package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemNameDto;
import ru.practicum.shareit.item.model.Item;

/**
 * Класс для преобразования объектов типа Item в тип ItemDto и обратно.
 */
public class ItemMapper {

    public static Item mapItemDtoToItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .requestId(itemDto.getRequestId())
                .owner(itemDto.getOwner())
                .build();
    }

    public static Item mapItemNameDtoToItem(ItemNameDto itemNameDto) {
        return Item.builder()
                .id(itemNameDto.getId())
                .name(itemNameDto.getName())
                .requestId(itemNameDto.getRequestId())
                .owner(itemNameDto.getOwner())
                .build();
    }

    public static ItemDto mapToItemDtoWithComments(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setRequestId(item.getRequestId());
        itemDto.setOwner(item.getOwner());
        return itemDto;
    }

    public static ItemNameDto mapToItemNameDto(Item item) {
        return ItemNameDto.builder()
                .id(item.getId())
                .name(item.getName())
                .requestId(item.getRequestId())
                .owner(item.getOwner())
                .build();
    }
}