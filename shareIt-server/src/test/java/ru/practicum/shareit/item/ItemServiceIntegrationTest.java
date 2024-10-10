package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.booking.model.Booking;
import ru.practicum.shareit.server.booking.model.Status;
import ru.practicum.shareit.server.booking.storage.BookingRepository;
import ru.practicum.shareit.server.item.dto.CommentDto;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.item.service.ItemServiceImpl;
import ru.practicum.shareit.server.item.storage.CommentRepository;
import ru.practicum.shareit.server.item.storage.ItemRepository;
import ru.practicum.shareit.server.request.service.ItemRequestService;
import ru.practicum.shareit.server.user.model.User;
import ru.practicum.shareit.server.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ItemServiceIntegrationTest {

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRequestService requestService;

    private User user;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        commentRepository.deleteAll();
        bookingRepository.deleteAll();

        user = new User(null, "Test User", "testuser@example.com");
        userRepository.save(user);
    }

    @Test
    void getAll_ReturnsAllItems_WhenCalled() {
        Item item1 = new Item(null, "Item 1", "Description 1", true, user);
        Item item2 = new Item(null, "Item 2", "Description 2", true, user);
        itemRepository.save(item1);
        itemRepository.save(item2);

        Collection<ItemDto> items = itemService.getAll();

        assertThat(items).hasSize(2);
    }

    @Test
    void getAllItemsByUserId_ReturnsUserItems_WhenCalledWithValidUserId() {
        Item item1 = new Item(null, "User Item 1", "Description 1", true, user);
        Item item2 = new Item(null, "User Item 2", "Description 2", true, user);
        itemRepository.save(item1);
        itemRepository.save(item2);

        List<ItemDto> items = itemService.getAllItemsByUserId(user.getId());

        assertThat(items).hasSize(2);
    }

    @Test
    void getItemById_ReturnsItem_WhenCalledWithValidId() {
        Item item = new Item(null, "Item", "Description", true, user);
        itemRepository.save(item);

        ItemDto foundItem = itemService.getItemById(item.getId());

        assertThat(foundItem.getName()).isEqualTo(item.getName());
    }

    @Test
    void addItem_ReturnsAddedItem_WhenCalledWithValidData() {
        ItemDto newItemDto = new ItemDto(null, "New Item", "New Description", true,
                null);

        ItemDto addedItem = itemService.addItem(user.getId(), newItemDto);

        assertThat(addedItem.getName()).isEqualTo(newItemDto.getName());
        assertThat(addedItem.getDescription()).isEqualTo(newItemDto.getDescription());
    }

    @Test
    void updateItem_ReturnsUpdatedItem_WhenCalledWithValidData() {
        Item item = new Item(null, "Old Item", "Old Description", true, user);
        itemRepository.save(item);

        ItemDto updatedItemDto = new ItemDto(item.getId(), "Updated Item", "Updated Description",
                true, null);
        ItemDto updatedItem = itemService.updateItem(user.getId(), item.getId(), updatedItemDto);

        assertThat(updatedItem.getName()).isEqualTo(updatedItemDto.getName());
        assertThat(updatedItem.getDescription()).isEqualTo(updatedItemDto.getDescription());
    }

    @Test
    void removeItemById_RemovesItem_WhenCalledWithValidId() {
        Item item = new Item(null, "Item to Remove", "Description", true, user);
        itemRepository.save(item);

        itemService.removeItemById(item.getId(), user.getId());

        assertThat(itemRepository.findById(item.getId())).isEmpty();
    }

    @Test
    void createComment_AddsComment_WhenCalledWithValidData() {
        Item item = new Item(null, "Item", "Description", true, user);
        itemRepository.save(item);

        User commenter = new User(null, "Commenter", "commenter@example.com");
        userRepository.save(commenter);

        Booking booking = new Booking(null, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(1),
                item, commenter, Status.APPROVED);
        bookingRepository.save(booking);

        CommentDto commentDto = new CommentDto(null, "Nice item!", null, null);
        CommentDto createdComment = itemService.createComment(commentDto, item.getId(), commenter.getId());

        assertThat(createdComment.getText()).isEqualTo(commentDto.getText());
    }
}