package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.exception.ValidationException;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.server.request.model.ItemRequest;
import ru.practicum.shareit.server.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.server.request.storage.ItemRequestRepository;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.mapper.UserMapper;
import ru.practicum.shareit.server.user.model.User;
import ru.practicum.shareit.server.user.service.UserService;
import ru.practicum.shareit.server.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {
    @Mock
    ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    ItemRequestServiceImpl itemRequestService;

    @Test
    void getAllItemRequestsByUserId_ValidUser_ReturnsRequests() {
        Long userId = 1L;
        User user = new User(userId, "TestUserName", "user@email.ru");
        ItemRequest itemRequest = new ItemRequest(1L, "Need a chair", user, LocalDateTime.now(),
                new ArrayList<>());
        List<ItemRequest> requests = List.of(itemRequest);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.getAllByRequester_Id(userId)).thenReturn(requests);

        List<ItemRequestDto> result = itemRequestService.getAllItemRequestsByUserId(userId);

        assertEquals(1, result.size());
        assertEquals(itemRequest.getDescription(), result.get(0).getDescription());
        verify(userRepository).findById(userId);
        verify(itemRequestRepository).getAllByRequester_Id(userId);
    }

    @Test
    void getAllItemRequestsByUserId_NonExistentUser_ThrowsNotFoundException() {
        Long userId = 777L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            itemRequestService.getAllItemRequestsByUserId(userId);
        });

        assertEquals("Пользователь с id = " + userId + " не найден", exception.getMessage());
        verify(userRepository).findById(userId);
        verify(itemRequestRepository, never()).getAllByRequester_Id(any());
    }

    @Test
    void addItemRequest_ValidData_ReturnsAddedRequest() {
        Long userId = 1L;
        User requester = new User();
        requester.setId(userId);
        requester.setName("userName");
        requester.setEmail("user@email.ru");

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Need a table");
        itemRequestDto.setRequester(requester);

        when(userService.getUserById(userId)).thenReturn(UserMapper.mapToUserDto(requester));

        ItemRequest itemRequest = ItemRequestMapper.mapToItemRequest(itemRequestDto, requester);
        itemRequest.setCreated(LocalDateTime.now());

        when(itemRequestRepository.save(any())).thenReturn(itemRequest);

        ItemRequestDto result = itemRequestService.addItemRequest(userId, itemRequestDto);

        assertNotNull(result);
        assertEquals(itemRequestDto.getDescription(), result.getDescription());
        assertEquals(requester, result.getRequester());

        verify(itemRequestRepository, times(1)).save(any()); // Ожидаем 1 вызов
    }

    @Test
    void addItemRequest_NonExistentUser_ThrowsNotFoundException() {
        Long userId = 999L;
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Need a chair");

        when(userService.getUserById(userId)).thenThrow(new NotFoundException("Пользователь с id " + userId + " не найден!"));

        assertThrows(NotFoundException.class, () -> itemRequestService.addItemRequest(userId, itemRequestDto));

        verify(userService).getUserById(userId);
        verify(itemRequestRepository, never()).save(any());
    }

    @Test
    void addItemRequest_InvalidDto_ThrowsValidationException() {
        Long userId = 1L;
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("");

        assertThrows(ValidationException.class, () -> itemRequestService.addItemRequest(userId, itemRequestDto));

        verify(userService, never()).getUserById(userId);
        verify(itemRequestRepository, never()).save(any());
    }

    @Test
    void getItemRequestById_ValidRequestId_ReturnsItemRequestDto() {
        Long userId = 1L;
        Long itemRequestId = 7L;

        User user = new User();
        user.setId(userId);
        user.setName("userName");
        user.setEmail("user@email.ru");

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(itemRequestId);
        itemRequest.setDescription("Need a cup");
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now());

        when(userService.getUserById(userId)).thenReturn(UserMapper.mapToUserDto(user));
        when(itemRequestRepository.findById(itemRequestId)).thenReturn(Optional.of(itemRequest));

        ItemRequestDto result = itemRequestService.getItemRequestById(itemRequestId, userId);

        assertNotNull(result);
        assertEquals(itemRequestId, result.getId());
        assertEquals(itemRequest.getDescription(), result.getDescription());
        assertEquals(itemRequest.getRequester(), result.getRequester());
        verify(userService).getUserById(userId);
        verify(itemRequestRepository).findById(itemRequestId);
    }

    @Test
    void getItemRequestById_NonExistentRequest_ThrowsNotFoundException() {
        Long userId = 1L;
        Long itemRequestId = 10L;

        when(userService.getUserById(userId)).thenReturn(new UserDto());

        when(itemRequestRepository.findById(itemRequestId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequestById(itemRequestId, userId));

        verify(userService).getUserById(userId);
        verify(itemRequestRepository).findById(itemRequestId);
    }

    @Test
    void getItemRequestById_NonExistentUser_ThrowsNotFoundException() {
        Long userId = 777L;
        Long itemRequestId = 10L;

        when(userService.getUserById(userId)).thenThrow(new NotFoundException("Пользователь с id = " + userId
                + " не найден!"));

        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequestById(itemRequestId, userId));

        verify(userService).getUserById(userId);
        verify(itemRequestRepository, never()).findById(itemRequestId);
    }

    @Test
    void getAllRequests_NoPagination_ReturnsFilteredRequests() {
        Long userId = 1L;

        User requester1 = new User();
        requester1.setId(2L);
        requester1.setName("user1");

        User requester2 = new User();
        requester2.setId(3L);
        requester2.setName("user2");

        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setId(10L);
        itemRequest1.setDescription("Request 1");
        itemRequest1.setRequester(requester1);

        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setId(20L);
        itemRequest2.setDescription("Request 2");
        itemRequest2.setRequester(requester2);

        List<ItemRequest> requests = List.of(itemRequest1, itemRequest2);

        when(userService.getUserById(userId)).thenReturn(new UserDto());
        when(itemRequestRepository.findAll()).thenReturn(requests);

        List<ItemRequestDto> result = itemRequestService.getAllRequests(userId, null, null);

        assertEquals(2, result.size());
        assertEquals("Request 1", result.get(0).getDescription());
        assertEquals("Request 2", result.get(1).getDescription());
        verify(userService).getUserById(userId);
        verify(itemRequestRepository).findAll();
    }

    @Test
    void getAllRequests_WithPagination_ReturnsPagedFilteredRequests() {
        Long userId = 1L;

        User requester1 = new User();
        requester1.setId(2L);
        requester1.setName("user1");

        User requester2 = new User();
        requester2.setId(3L);
        requester2.setName("user2");

        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setId(10L);
        itemRequest1.setDescription("Request 1");
        itemRequest1.setRequester(requester1);

        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setId(20L);
        itemRequest2.setDescription("Request 2");
        itemRequest2.setRequester(requester2);

        List<ItemRequest> requests = List.of(itemRequest1, itemRequest2);
        Pageable pageable = PageRequest.of(0, 2);
        Page<ItemRequest> requestPage = new PageImpl<>(requests, pageable, requests.size());

        when(userService.getUserById(userId)).thenReturn(new UserDto());
        when(itemRequestRepository.findAll(pageable)).thenReturn(requestPage);

        List<ItemRequestDto> result = itemRequestService.getAllRequests(userId, 0, 2);

        assertEquals(2, result.size());
        assertEquals("Request 1", result.get(0).getDescription());
        assertEquals("Request 2", result.get(1).getDescription());
        verify(userService).getUserById(userId);
        verify(itemRequestRepository).findAll(pageable);
    }

    @Test
    void getAllRequests_NonExistentUser_ThrowsNotFoundException() {
        Long userId = 888L;

        when(userService.getUserById(userId)).thenThrow(new NotFoundException("Пользователь с id = " + userId
                + " не найден!"));

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getAllRequests(userId, null, null));

        verify(userService).getUserById(userId);
        verify(itemRequestRepository, never()).findAll();
    }

    @Test
    void getAllRequests_InvalidPageParameters_ThrowsValidationException() {
        Long userId = 1L;

        when(userService.getUserById(userId)).thenReturn(new UserDto());

        assertThrows(ValidationException.class,
                () -> itemRequestService.getAllRequests(userId, -1, 0));

        verify(userService).getUserById(userId);
        verify(itemRequestRepository, never()).findAll();
    }

    @Test
    void addItemToRequest_ValidRequestId_AddsItemSuccessfully() {
        Long requestId = 1L;
        ItemDto itemDto = new ItemDto();
        itemDto.setRequestId(requestId);
        itemDto.setName("NewItem");

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(requestId);
        itemRequest.setDescription("RequestDescription");
        itemRequest.setItems(new ArrayList<>());

        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));

        itemRequestService.addItemToRequest(itemDto);

        assertEquals(1, itemRequest.getItems().size());
        assertEquals("NewItem", itemRequest.getItems().iterator().next().getName());
        verify(itemRequestRepository).findById(requestId);
    }

    @Test
    void addItemToRequest_NonExistentRequest_ThrowsNotFoundException() {
        Long requestId = 1L;
        ItemDto itemDto = new ItemDto();
        itemDto.setRequestId(requestId);
        itemDto.setName("NewItem");

        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.addItemToRequest(itemDto));

        verify(itemRequestRepository).findById(requestId);
    }
}