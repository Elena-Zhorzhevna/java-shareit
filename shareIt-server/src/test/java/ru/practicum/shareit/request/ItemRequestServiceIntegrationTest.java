package ru.practicum.shareit.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.exception.ValidationException;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.request.model.ItemRequest;
import ru.practicum.shareit.server.request.service.ItemRequestService;
import ru.practicum.shareit.server.request.storage.ItemRequestRepository;
import ru.practicum.shareit.server.user.model.User;
import ru.practicum.shareit.server.user.service.UserService;
import ru.practicum.shareit.server.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;


@SpringBootTest
@Transactional
public class ItemRequestServiceIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @BeforeEach
    public void setUp() {
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getAllItemRequestsByUserId_ReturnsRequests_WhenUserExists() {
        User user = new User(null, "User", "user@email.com");
        userRepository.save(user);

        ItemRequest itemRequest1 = new ItemRequest(null, "Request 1", user, LocalDateTime.now());
        ItemRequest itemRequest2 = new ItemRequest(null, "Request 2", user, LocalDateTime.now());
        itemRequestRepository.save(itemRequest1);
        itemRequestRepository.save(itemRequest2);

        List<ItemRequestDto> requests = itemRequestService.getAllItemRequestsByUserId(user.getId());

        assertThat(requests).hasSize(2);
        assertThat(requests).extracting(ItemRequestDto::getDescription)
                .containsExactlyInAnyOrder("Request 1", "Request 2");
    }

    @Test
    void getAllItemRequestsByUserId_ThrowsNotFoundException_WhenUserDoesNotExist() {
        assertThrows(NotFoundException.class, () -> itemRequestService.getAllItemRequestsByUserId(999L));
    }

    @Test
    void addItemRequest_ReturnsAddedRequest_WhenRequestIsValid() {
        User user = new User(null, "User", "user@email.com");
        userRepository.save(user);
        ItemRequestDto requestDto = new ItemRequestDto(null, "New Request", null, LocalDateTime.now(), null);

        ItemRequestDto addedRequest = itemRequestService.addItemRequest(user.getId(), requestDto);

        assertThat(addedRequest.getDescription()).isEqualTo(requestDto.getDescription());
        assertThat(itemRequestRepository.count()).isEqualTo(1);
    }

    @Test
    void addItemRequest_ThrowsValidationException_WhenDescriptionIsNull() {
        User user = new User(null, "User", "user@email.com");
        userRepository.save(user);
        ItemRequestDto requestDto = new ItemRequestDto(null, null, null, null, null);

        assertThrows(ValidationException.class, () -> itemRequestService.addItemRequest(user.getId(), requestDto));
    }

    @Test
    void getItemRequestById_ReturnsRequest_WhenRequestExists() {
        User user = new User(null, "User", "user@email.com");
        userRepository.save(user);
        ItemRequest itemRequest = new ItemRequest(null, "Request", user, LocalDateTime.now());
        ItemRequest savedRequest = itemRequestRepository.save(itemRequest);

        ItemRequestDto foundRequest = itemRequestService.getItemRequestById(savedRequest.getId(), user.getId());

        assertThat(foundRequest.getDescription()).isEqualTo(itemRequest.getDescription());
    }

    @Test
    void getItemRequestById_ThrowsNotFoundException_WhenRequestDoesNotExist() {
        User user = new User(null, "User", "user@email.com");
        userRepository.save(user);

        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequestById(999L, user.getId()));
    }

    @Test
    void getAllRequests_ReturnsRequests_WhenCalledWithValidUserId() {
        User user1 = new User(null, "User 1", "user1@email.ru");
        User user2 = new User(null, "User 2", "user2@email.ru");
        userRepository.save(user1);
        userRepository.save(user2);

        ItemRequest itemRequest1 = new ItemRequest(null, "Request 1", user2, LocalDateTime.now());
        ItemRequest itemRequest2 = new ItemRequest(null, "Request 2", user2, LocalDateTime.now());
        itemRequestRepository.save(itemRequest1);
        itemRequestRepository.save(itemRequest2);

        List<ItemRequestDto> requests = itemRequestService.getAllRequests(user1.getId(), null, null);

        assertThat(requests).hasSize(2);
        assertThat(requests).extracting(ItemRequestDto::getDescription)
                .containsExactlyInAnyOrder("Request 1", "Request 2");
    }

    @Test
    void getAllRequests_ThrowsNotFoundException_WhenUserDoesNotExist() {
        assertThrows(NotFoundException.class, () -> itemRequestService.getAllRequests(999L, null, null));
    }

    @Test
    void addItemToRequest_AddsItemToRequest_WhenRequestExists() {
        User user = new User(null, "User", "user@example.com");
        userRepository.save(user);
        ItemRequest itemRequest = new ItemRequest(null, "Request", user, LocalDateTime.now());
        ItemRequest savedRequest = itemRequestRepository.save(itemRequest);
        ItemDto itemDto = new ItemDto(null, "Item", "Description", savedRequest.getId());

        itemRequestService.addItemToRequest(itemDto);

        assertThat(savedRequest.getItems()).hasSize(1);
    }

    @Test
    void addItemToRequest_ThrowsNotFoundException_WhenRequestDoesNotExist() {
        ItemDto itemDto = new ItemDto(null, "ItemName", "Description", 500L);
        assertThrows(NotFoundException.class, () -> itemRequestService.addItemToRequest(itemDto));
    }
}




/*

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.server.exception.NotFoundException;
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
import java.util.List;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
public class ItemRequestServiceIntegrationTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    @Autowired
    private ItemRequestServiceImpl itemRequestService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllItemRequestsByUserId_ReturnsRequests() {
        Long userId = 1L;
        User user = new User(userId, "User", "user@email.com");
        ItemRequest request1 = new ItemRequest(1L, "Request 1", user, LocalDateTime.now());
        ItemRequest request2 = new ItemRequest(2L, "Request 2", user, LocalDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.getAllByRequester_Id(userId)).thenReturn(List.of(request1, request2));

        List<ItemRequestDto> requests = itemRequestService.getAllItemRequestsByUserId(userId);

        assertThat(requests).hasSize(2);
        assertThat(requests.get(0).getDescription()).isEqualTo("Request 1");
        assertThat(requests.get(1).getDescription()).isEqualTo("Request 2");
    }

    @Test
    void addItemRequest_ValidRequest_ReturnsAddedRequest() {
        Long userId = 1L;
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Need a table");

        // Создание объекта пользователя
        User user = new User(userId, "User", "user@email.com");
        UserDto userDto = UserMapper.mapToUserDto(user);

        // Настройка мока для userService
        when(userService.getUserById(userId)).thenReturn(userDto);

        // Создание объекта ItemRequest
        ItemRequest itemRequest = ItemRequestMapper.mapToItemRequest(itemRequestDto, user);
        itemRequest.setCreated(LocalDateTime.now());

        // Настройка мока для itemRequestRepository
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        // Вызов метода и проверка результата
        ItemRequestDto addedRequest = itemRequestService.addItemRequest(userId, itemRequestDto);

        assertThat(addedRequest.getDescription()).isEqualTo(itemRequestDto.getDescription());
        assertThat(addedRequest.getRequester()).isEqualTo(userDto);
    }


    @Test
    void getItemRequestById_ValidId_ReturnsRequest() {
        Long requestId = 1L;
        Long userId = 1L;
        ItemRequest itemRequest = new ItemRequest(requestId, "Request description",
                new User(userId, "User", "user@example.com"), LocalDateTime.now());

        when(userService.getUserById(userId)).thenReturn(UserMapper.mapToUserDto(itemRequest.getRequester()));
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));

        ItemRequestDto result = itemRequestService.getItemRequestById(requestId, userId);

        assertThat(result.getDescription()).isEqualTo(itemRequest.getDescription());
    }

    @Test
    void getItemRequestById_NonExistingId_ThrowsNotFoundException() {
        Long requestId = 999L;
        Long userId = 1L;

        when(userService.getUserById(userId)).thenReturn(new UserDto());

        assertThrows(NotFoundException.class, () -> {
            itemRequestService.getItemRequestById(requestId, userId);
        });
    }

    @Test
    void addItemToRequest_ValidItemDto_AddsItem() {
        Long requestId = 1L;
        ItemDto itemDto = new ItemDto();
        itemDto.setRequestId(requestId);

        ItemRequest itemRequest = new ItemRequest(requestId, "Request description",
                new User(1L, "User", "user@example.com"), LocalDateTime.now());

        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));

        itemRequestService.addItemToRequest(itemDto);

        assertThat(itemRequest.getItems()).hasSize(1);
    }
}*/
