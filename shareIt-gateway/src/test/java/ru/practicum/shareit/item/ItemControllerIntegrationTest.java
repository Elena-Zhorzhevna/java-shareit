package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.CommentDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerIntegrationTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ItemClient itemClient;

    @SneakyThrows
    @Test
    void createItem_whenValidRequest_thenReturnStatusIsCreated() {
        ItemDto itemDto = new ItemDto("Item Name", "Item Description", true);

        when(itemClient.createItem(any(Long.class), any(ItemDto.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(itemDto));

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Item Name"));
    }

    @SneakyThrows
    @Test
    void createItem_whenInvalidRequest_thenReturnStatusIsBadRequest() {
        ItemDto itemDto = new ItemDto("", "Item Description", true);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void updateItem_whenValidRequest_thenReturnStatusIsOk() {
        ItemDto newItemDto = new ItemDto("Updated Item Name", "Updated Item Description", true);

        when(itemClient.updateItem(eq(1L), eq(1L), any(ItemDto.class)))
                .thenReturn(ResponseEntity.ok(newItemDto));

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItemDto)))
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Updated Item Name"));
    }

    @SneakyThrows
    @Test
    void getAllItems_whenValidRequest_thenReturnStatusIsOk() {
        when(itemClient.getAllItemsByUserId(1L))
                .thenReturn(ResponseEntity.ok(List.of(new ItemDto("Item Name", "Item Description",
                        true))));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Item Name"));
    }

    @SneakyThrows
    @Test
    void getItemById_whenValidRequest_thenReturnStatusIsOk() {
        ItemDto itemDto = new ItemDto("Item Name", "Item Description", true);

        when(itemClient.getItemById(eq(1L), any(Long.class)))
                .thenReturn(ResponseEntity.ok(itemDto));

        mockMvc.perform(get("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Item Name"));
    }

    @SneakyThrows
    @Test
    void searchItems_whenValidRequest_thenReturnStatusIsOk() {
        when(itemClient.getBySearch(1L, "search text"))
                .thenReturn(ResponseEntity.ok(List.of(new ItemDto("Item Name", "Item Description",
                        true))));

        mockMvc.perform(get("/items/search?text=search text")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Item Name"));
    }

    @SneakyThrows
    @Test
    void createComment_whenValidRequest_thenReturnStatusIsCreated() {
        CommentDto commentDto = new CommentDto("Super item!");

        when(itemClient.createComment(any(CommentDto.class), eq(1L), eq(1L)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(commentDto));

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.text").value("Super item!"));
    }
}