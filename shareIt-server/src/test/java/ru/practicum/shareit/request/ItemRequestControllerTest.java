package ru.practicum.shareit.request;

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
import ru.practicum.shareit.server.request.controller.ItemRequestController;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.request.service.ItemRequestService;


import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
public class ItemRequestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ItemRequestService itemRequestService;

    @InjectMocks
    private ItemRequestController itemRequestController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(itemRequestController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getAllItemRequestsFromUser_ValidUserId_ReturnsItemRequests() throws Exception {
        Long userId = 1L;
        ItemRequestDto itemRequestDto1 = new ItemRequestDto();
        itemRequestDto1.setDescription("Request 1");

        ItemRequestDto itemRequestDto2 = new ItemRequestDto();
        itemRequestDto2.setDescription("Request 2");

        when(itemRequestService.getAllItemRequestsByUserId(userId)).thenReturn(List.of(itemRequestDto1, itemRequestDto2));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(itemRequestDto1, itemRequestDto2))));

        verify(itemRequestService).getAllItemRequestsByUserId(userId);
    }

    @Test
    void getAllItemRequestsFromUser_NonExistentUser_ReturnsEmptyCollection() throws Exception {
        Long userId = 7L;

        when(itemRequestService.getAllItemRequestsByUserId(userId)).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(itemRequestService).getAllItemRequestsByUserId(userId);
    }

    @Test
    void getAllRequests_ValidUserIdAndNoPagination_ReturnsRequests() throws Exception {
        Long userId = 1L;
        ItemRequestDto itemRequestDto1 = new ItemRequestDto();
        itemRequestDto1.setDescription("Request 3");

        ItemRequestDto itemRequestDto2 = new ItemRequestDto();
        itemRequestDto2.setDescription("Request 4");

        when(itemRequestService.getAllRequests(userId, null, null))
                .thenReturn(List.of(itemRequestDto1, itemRequestDto2));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(itemRequestDto1, itemRequestDto2))));

        verify(itemRequestService).getAllRequests(userId, null, null);
    }

    @Test
    void getAllRequests_NonExistentUser_ReturnsEmptyList() throws Exception {
        Long userId = 9L;

        when(itemRequestService.getAllRequests(userId, null, null)).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(itemRequestService).getAllRequests(userId, null, null);
    }

    @Test
    void createNewItemRequestThenNewItemRequestDtoReturned() throws Exception {
        Long userId = 1L;
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Need a new chair");

        ItemRequestDto expectedResponse = new ItemRequestDto();
        expectedResponse.setId(1L);
        expectedResponse.setDescription("Need a new chair");

        when(itemRequestService.addItemRequest(eq(userId), any(ItemRequestDto.class)))
                .thenReturn(expectedResponse);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));

        verify(itemRequestService).addItemRequest(eq(userId), any(ItemRequestDto.class));
    }

    @Test
    void getItemRequestByIdThenItemRequestDtoReturned() throws Exception {
        Long itemRequestId = 1L;
        Long userId = 2L;
        ItemRequestDto expectedItemRequestDto = new ItemRequestDto();
        expectedItemRequestDto.setId(itemRequestId);
        expectedItemRequestDto.setDescription("Need a new book");

        when(itemRequestService.getItemRequestById(itemRequestId, userId)).thenReturn(expectedItemRequestDto);

        mockMvc.perform(get("/requests/{id}", itemRequestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedItemRequestDto)));

        verify(itemRequestService).getItemRequestById(itemRequestId, userId);
    }


/*    @Test
    void getAllRequests_ValidUserIdAndNoPagination_ReturnsRequests() throws Exception {
        Long userId = 1L;
        ItemRequestDto itemRequestDto1 = new ItemRequestDto();
        itemRequestDto1.setDescription("Request 1");

        ItemRequestDto itemRequestDto2 = new ItemRequestDto();
        itemRequestDto2.setDescription("Request 2");

        when(itemRequestService.getAllRequests(userId, null, null)).thenReturn(List.of(itemRequestDto1, itemRequestDto2));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(itemRequestDto1, itemRequestDto2))));

        verify(itemRequestService).getAllRequests(userId, null, null);
    }*/

/*    @Test
    void getAllRequests_NonExistentUser_ReturnsEmptyList() throws Exception {
        Long userId = 9L;

        when(itemRequestService.getAllRequests(userId, null, null)).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(itemRequestService).getAllRequests(userId, null, null);
    }*/

    @Test
    void getAllRequests_ValidUserIdWithPagination_ReturnsRequests() throws Exception {
        Long userId = 1L;
        Integer pageNum = 0;
        Integer pageSize = 10;
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("RequestWithPagination");

        when(itemRequestService.getAllRequests(userId, pageNum, pageSize)).thenReturn(List.of(itemRequestDto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(pageNum))
                        .param("size", String.valueOf(pageSize)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(itemRequestDto))));

        verify(itemRequestService).getAllRequests(userId, pageNum, pageSize);
    }
}