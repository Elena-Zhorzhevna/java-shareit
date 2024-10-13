package ru.practicum.shareit.request;

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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerIntegrationTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ItemRequestClient itemRequestClient;

    @SneakyThrows
    @Test
    void createItemRequest_whenValidRequest_thenReturnStatusIsCreated() {
        ItemRequestDto itemRequestDto = new ItemRequestDto("Request Description");

        when(itemRequestClient.createItemRequest(any(Long.class), any(ItemRequestDto.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(itemRequestDto));

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                //.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.description").value("Request Description"));
    }

    @SneakyThrows
    @Test
    void getItemRequestsByUserId_whenValidRequest_thenReturnStatusIsOk() {
        List<ItemRequestDto> itemRequestDtos = List.of(new ItemRequestDto("Request Description"));

        when(itemRequestClient.getAllItemRequestsByUserId(1L))
                .thenReturn(ResponseEntity.ok(itemRequestDtos));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].description").value("Request Description"));
    }

    @SneakyThrows
    @Test
    void getItemRequestsByOtherUsers_whenValidRequest_thenReturnStatusIsOk() {
        List<ItemRequestDto> itemRequestDtos = List.of(new ItemRequestDto("Other User Request"));

        when(itemRequestClient.getItemRequestsByOtherUsers(1L))
                .thenReturn(ResponseEntity.ok(itemRequestDtos));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L))
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].description").value("Other User Request"));
    }

    @SneakyThrows
    @Test
    void getItemRequestById_whenValidRequest_thenReturnStatusIsOk() {
        ItemRequestDto itemRequestDto = new ItemRequestDto("Request Description");

        when(itemRequestClient.getItemRequestById(eq(1L), any(Long.class)))
                .thenReturn(ResponseEntity.ok(itemRequestDto));

        mockMvc.perform(get("/requests/{requestId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.description").value("Request Description"));
    }
}
