package ru.practicum.shareit.item;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;

import ru.practicum.shareit.server.booking.storage.BookingRepository;

import ru.practicum.shareit.server.exception.ValidationException;
import ru.practicum.shareit.server.item.dto.CommentDto;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.mapper.ItemMapper;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.item.service.ItemServiceImpl;
import ru.practicum.shareit.server.item.storage.CommentRepository;
import ru.practicum.shareit.server.item.storage.ItemRepository;
import ru.practicum.shareit.server.request.service.ItemRequestService;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.mapper.UserMapper;
import ru.practicum.shareit.server.user.model.User;
import ru.practicum.shareit.server.user.service.UserService;
import ru.practicum.shareit.server.user.storage.UserRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ItemServiceTest {

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRequestService requestService;

    private Item item;
    private ItemDto itemDto;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User(1L, "User1", "user1@example.com");
        item = new Item(1L, "Item1", "Description1", true, user);
        itemDto = new ItemDto(1L, "Item1", "Description1", true, null);
    }

    @Test
    void getAll_ReturnsAllItems() {
        when(itemRepository.findAll()).thenReturn(List.of(item));

        Collection<ItemDto> items = itemService.getAll();

        assertEquals(1, items.size());
        assertEquals("Item1", items.iterator().next().getName());
        verify(itemRepository, times(1)).findAll();
    }

    @Test
    void getAllItemsByUserId_ReturnsItems() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findByOwnerId(1L)).thenReturn(List.of(item));

        List<ItemDto> items = itemService.getAllItemsByUserId(1L);

        assertEquals(1, items.size());
        verify(userRepository, times(1)).findById(1L);
        verify(itemRepository, times(1)).findByOwnerId(1L);
    }

    @Test
    void getItemById_ReturnsItem() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ItemDto foundItem = itemService.getItemById(1L);

        assertEquals("Item1", foundItem.getName());
        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    void searchItemsByText_ReturnsFilteredItems() {
        when(itemRepository.findAll()).thenReturn(List.of(item));

        Collection<ItemDto> result = itemService.searchItemsByText("Item");

        assertEquals(1, result.size());
        assertTrue(result.stream().anyMatch(i -> i.getName().equals("Item1")));
    }

    @Test
    void addItem_AddsItem() {
        Long userId = 1L;
        ItemDto itemDto = new ItemDto(null, "Item1", "Description1", true, null);

        UserDto userDto = new UserDto();
        userDto.setId(userId);
        userDto.setName("User1");
        userDto.setEmail("user1@example.com");

        User user = UserMapper.mapUserDtoToUser(userDto);

        assertNotNull(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Item item = ItemMapper.mapItemDtoToItem(itemDto);
        item.setOwner(user);

        when(itemRepository.save(any(Item.class))).thenReturn(item);

        when(userService.getUserById(userId)).thenReturn(userDto);

        ItemDto result = itemService.addItem(userId, itemDto);

        assertNotNull(result);
        assertEquals("Item1", result.getName());
        verify(itemRepository, times(1)).save(any(Item.class));
    }


    @Test
    void updateItem_UpdatesItem() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto updatedItem = itemService.updateItem(1L, 1L, itemDto);

        assertEquals("Item1", updatedItem.getName());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void removeAllItemsByOwnerId_RemovesItems() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        itemService.removeAllItemsByOwnerId(1L);

        verify(itemRepository, times(1)).removeItemByOwnerId(1L);
    }

    @Test
    void removeItemById_RemovesItem() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        itemService.removeItemById(1L, 1L);

        verify(itemRepository, times(1)).removeItemByIdAndOwnerId(1L, 1L);
    }

    @Test
    void createComment_CreatesComment() {
        CommentDto commentDto = new CommentDto(1L, "Nice item!", null, null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.getAllByBookerId(1L)).thenReturn(Collections.emptyList()); // No bookings

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            itemService.createComment(commentDto, 1L, 1L);
        });
        assertEquals("Бронирование вещи не подверждено, нельзя добавить комментарий.", exception.getMessage());
    }

    @Test
    void getCommentsByItemId_ReturnsComments() {
        when(commentRepository.findAllByItemId(eq(1L), any(Sort.class))).thenReturn(Collections.emptyList());

        List<CommentDto> comments = itemService.getCommentsByItemId(1L);

        assertEquals(0, comments.size());

        verify(commentRepository, times(1)).findAllByItemId(eq(1L), any(Sort.class));
    }

    @Test
    void getAll_ReturnsEmptyList_WhenNoItemsExist() {
        when(itemRepository.findAll()).thenReturn(Collections.emptyList());

        Collection<ItemDto> items = itemService.getAll();

        assertEquals(0, items.size());
        verify(itemRepository, times(1)).findAll();
    }

    @Test
    void getAllItemsByUserId_ReturnsEmptyList_WhenUserHasNoItems() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findByOwnerId(1L)).thenReturn(Collections.emptyList());

        List<ItemDto> items = itemService.getAllItemsByUserId(1L);

        assertEquals(0, items.size());
        verify(userRepository, times(1)).findById(1L);
        verify(itemRepository, times(1)).findByOwnerId(1L);
    }
}
