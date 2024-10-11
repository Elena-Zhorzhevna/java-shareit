package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.dto.ItemNameDto;
import ru.practicum.shareit.server.item.mapper.ItemMapper;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

public class ItemMapperTest {

    @Test
    void mapItemDtoToItem_ShouldMapFieldsCorrectly() {

        ItemDto itemDto = new ItemDto(1L, "Item1", "Description1", true, 2L);

        Item item = ItemMapper.mapItemDtoToItem(itemDto);

        assertEquals(itemDto.getId(), item.getId());
        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getDescription(), item.getDescription());
        assertEquals(itemDto.getAvailable(), item.getAvailable());
        assertEquals(itemDto.getRequestId(), item.getRequestId());
        assertNull(item.getOwner());
    }

    @Test
    void mapToItemDtoWithComments_ShouldMapFieldsCorrectly() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item1");
        item.setDescription("Description1");
        item.setAvailable(true);
        item.setRequestId(2L);
        item.setOwner(new User());

        ItemDto itemDto = ItemMapper.mapToItemDtoWithComments(item);

        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
        assertEquals(item.getRequestId(), itemDto.getRequestId());
        assertEquals(item.getOwner(), itemDto.getOwner());
    }

    @Test
    void mapToItemNameDto_ShouldMapFieldsCorrectly() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item1");
        item.setRequestId(2L);
        item.setOwner(new User());

        ItemNameDto itemNameDto = ItemMapper.mapToItemNameDto(item);

        assertEquals(item.getId(), itemNameDto.getId());
        assertEquals(item.getName(), itemNameDto.getName());
        assertEquals(item.getRequestId(), itemNameDto.getRequestId());
        assertEquals(item.getOwner(), itemNameDto.getOwner());
    }
}
