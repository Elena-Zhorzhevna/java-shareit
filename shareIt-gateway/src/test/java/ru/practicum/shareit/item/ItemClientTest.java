/*
package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ItemClientTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ItemClient itemClient;

    @InjectMocks
    private ItemController itemController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateItem() throws Exception {
        ItemDto itemDto = new ItemDto("Item Name", "Item Description", true);
        ResponseEntity<Object> responseEntity = new ResponseEntity<>(itemDto, OK);

        when(itemClient.createItem(any(Long.class), any(ItemDto.class))).thenReturn(responseEntity);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Item Name"))
                .andExpect(jsonPath("$.description").value("Item Description"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    public void testGetAllItemsByUserId() throws Exception {
        List<ItemDto> itemList = List.of(new ItemDto("Item 1", "Description 1", true),
                new ItemDto("Item 2", "Description 2", false));
        ResponseEntity<Object> responseEntity = new ResponseEntity<>(itemList, OK);

        when(itemClient.getAllItemsByUserId(any(Long.class))).thenReturn(responseEntity);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Item 1"))
                .andExpect(jsonPath("$[1].description").value("Description 2"));
    }

    @Test
    public void testGetItemById() throws Exception {
        Long itemId = 1L;
        Long userId = 1L;

        // Создаем один и тот же объект для запроса и ответа
        ItemDto itemDto = new ItemDto("Item Name", "Item Description", true);
        ResponseEntity<Object> responseEntity = new ResponseEntity<>(itemDto, OK);

        // Настраиваем мок для метода getItemById
        when(itemClient.getItemById(eq(itemId), eq(userId))).thenReturn(responseEntity);

        // Выполняем запрос
        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));
    }



    @Test
    public void testUpdateItem() throws Exception {
        Long itemId = 1L;
        ItemDto newItemDto = new ItemDto("Updated Item", "Updated Description", true);
        ResponseEntity<Object> responseEntity = new ResponseEntity<>(newItemDto, OK);

        when(itemClient.updateItem(eq(itemId), any(Long.class), any(ItemDto.class))).thenReturn(responseEntity);

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newItemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Item"));
    }

    @Test
    public void testCreateComment() throws Exception {
        Long itemId = 1L;
        CommentDto commentDto = new CommentDto("Nice item!");
        ResponseEntity<Object> responseEntity = new ResponseEntity<>(commentDto, OK);

        when(itemClient.createComment(any(CommentDto.class), eq(itemId), any(Long.class))).thenReturn(responseEntity);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Nice item!"));
    }
}
*/
