package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.server.item.controller.ItemController;
import ru.practicum.shareit.server.item.dto.CommentDto;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.service.ItemService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(itemController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getAllItemsFromUser_ReturnsListOfItems() throws Exception {
        Long userId = 1L;
        List<ItemDto> items = List.of(new ItemDto(1L, "Item 1", "Description 1", true, null));

        when(itemService.getAllItemsByUserId(userId)).thenReturn(items);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Item 1"));

        verify(itemService, times(1)).getAllItemsByUserId(userId);
    }

    @Test
    void getItemById_ReturnsItem() throws Exception {
        Long itemId = 1L;
        ItemDto item = new ItemDto(itemId, "Item 1", "Description 1", true, null);

        when(itemService.getItemById(itemId)).thenReturn(item);

        mockMvc.perform(get("/items/{itemId}", itemId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Item 1"));

        verify(itemService, times(1)).getItemById(itemId);
    }

    @Test
    void searchItemsByText_ReturnsFoundItems() throws Exception {
        String text = "Item";
        List<ItemDto> items = List.of(new ItemDto(1L, "Item 1", "Description 1", true,
                null));

        when(itemService.searchItemsByText(text)).thenReturn(items);

        mockMvc.perform(get("/items/search")
                        .param("text", text))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Item 1"));

        verify(itemService, times(1)).searchItemsByText(text);
    }

    @Test
    void create_AddsItem() throws Exception {
        Long userId = 1L;
        ItemDto newItemDto = new ItemDto(null, "New Item", "New Description", true,
                null);
        ItemDto addedItemDto = new ItemDto(1L, "New Item", "New Description", true,
                null);

        when(itemService.addItem(userId, newItemDto)).thenReturn(addedItemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItemDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("New Item"));

        verify(itemService, times(1)).addItem(userId, newItemDto);
    }

    @Test
    void update_UpdatesItem() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        ItemDto updatedItemDto = new ItemDto(itemId, "Updated Item", "Updated Description",
                true, null);

        when(itemService.updateItem(userId, itemId, updatedItemDto)).thenReturn(updatedItemDto);

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedItemDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Updated Item"));

        verify(itemService, times(1)).updateItem(userId, itemId, updatedItemDto);
    }

    @Test
    void removeItemById_DeletesItem() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;

        mockMvc.perform(delete("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(itemService, times(1)).removeItemById(userId, itemId);
    }

    @Test
    void removeAllItemsByUser_DeletesAllItems() throws Exception {
        Long userId = 1L;

        mockMvc.perform(delete("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(itemService, times(1)).removeAllItemsByOwnerId(userId);
    }

    @Test
    void createComment_AddsComment() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Nice item!");

        CommentDto addedCommentDto = new CommentDto();
        addedCommentDto.setId(1L);
        addedCommentDto.setText("Nice item!");

        when(itemService.createComment(any(CommentDto.class), eq(itemId), eq(userId)))
                .thenReturn(addedCommentDto);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.text").value("Nice item!"));

        verify(itemService, times(1)).createComment(any(CommentDto.class), eq(itemId), eq(userId));
    }
}