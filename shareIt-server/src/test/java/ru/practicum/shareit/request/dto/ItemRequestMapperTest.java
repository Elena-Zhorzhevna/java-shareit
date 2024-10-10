package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.server.request.model.ItemRequest;
import ru.practicum.shareit.server.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemRequestMapperTest {

    @Test
    public void testMapToItemRequest() {
        // Arrange
        User requester = new User();
        requester.setId(1L);
        requester.setName("RequesterName");

        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(1L);
        dto.setDescription("RequestDescription");
        dto.setCreated(LocalDateTime.now());
        dto.setRequester(requester);

        ItemRequest itemRequest = ItemRequestMapper.mapToItemRequest(dto, requester);

        assertThat(itemRequest).isNotNull();
        assertThat(itemRequest.getId()).isEqualTo(dto.getId());
        assertThat(itemRequest.getDescription()).isEqualTo(dto.getDescription());
        assertThat(itemRequest.getRequester()).isEqualTo(requester);
        assertThat(itemRequest.getCreated()).isEqualTo(dto.getCreated());
        assertThat(itemRequest.getItems()).isNotNull().isEmpty();
    }

    @Test
    public void testMapToItemRequestDto() {
        User requester = new User();
        requester.setId(1L);
        requester.setName("RequesterName");

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("RequestDescription");
        itemRequest.setRequester(requester);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setItems(new ArrayList<>());

        ItemRequestDto dto = ItemRequestMapper.mapToItemRequestDto(itemRequest);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(itemRequest.getId());
        assertThat(dto.getDescription()).isEqualTo(itemRequest.getDescription());
        assertThat(dto.getRequester()).isEqualTo(requester);
        assertThat(dto.getCreated()).isEqualTo(itemRequest.getCreated());
        assertThat(dto.getItems()).isNotNull().isEmpty();
    }
}