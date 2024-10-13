package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.booking.dto.BookingDtoToPut;

import ru.practicum.shareit.server.booking.model.Booking;
import ru.practicum.shareit.server.booking.model.Status;
import ru.practicum.shareit.server.booking.service.BookingServiceImpl;
import ru.practicum.shareit.server.booking.storage.BookingRepository;
import ru.practicum.shareit.server.exception.InvalidRequestException;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.exception.ValidationException;

import ru.practicum.shareit.server.item.mapper.ItemMapper;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.item.service.ItemService;
import ru.practicum.shareit.server.item.storage.ItemRepository;

import ru.practicum.shareit.server.user.mapper.UserMapper;
import ru.practicum.shareit.server.user.model.User;
import ru.practicum.shareit.server.user.service.UserServiceImpl;
import ru.practicum.shareit.server.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private ItemService itemService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User user;
    private Item item;
    private User owner;
    private BookingDtoToPut bookingDtoToPut;
    private Booking booking;

    @BeforeEach
    void setUp() {
        user = new User(1L, "User", "test@email.ru");
        owner = new User(2L, "OwnerName", "owner@email.ru");
        item = new Item(1L, "Item", "Description", true, user);
        booking = new Booking(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item, owner,
                Status.WAITING);
    }

    @Test
    void getBookingsByUserIdWithState_ValidRequest_ReturnsBookingsList() {

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userService.getUserById(user.getId())).thenReturn(UserMapper.mapToUserDto(user));
        when(bookingRepository.getAllByBookerId(user.getId())).thenReturn(Collections.singletonList(booking));

        List<BookingDto> result = bookingService.getBookingsByUserIdWithState("ALL", user.getId());

        assertEquals(1, result.size());
    }


    @Test
    void getBookingsOfOwnerItems_ValidRequest_ReturnsBookingsList() {

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        when(itemRepository.findByOwnerId(user.getId())).thenReturn(Collections.singletonList(item));

        when(bookingRepository.getAllBookingsForItems(Collections.singletonList(item.getId())))
                .thenReturn(Collections.singletonList(booking));

        List<BookingDto> result = bookingService.getBookingsOfOwnerItems(user.getId(), "ALL");

        assertEquals(1, result.size());
    }

    @Test
    void create_ShouldCreateBooking_WhenValidData() {
        Long bookerId = 1L;
        Long itemId = 2L;
        BookingDtoToPut bookingDto = new BookingDtoToPut();
        bookingDto.setItemId(itemId);
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        User booker = new User();
        booker.setId(bookerId);

        User itemOwner = new User();
        itemOwner.setId(3L);

        Item item = new Item();
        item.setId(itemId);
        item.setAvailable(true);
        item.setOwner(itemOwner);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemService.getItemById(itemId)).thenReturn(ItemMapper.mapToItemDtoWithComments(item));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDto result = bookingService.create(bookingDto, bookerId);

        assertNotNull(result);
        verify(bookingRepository, times(1)).save(any());
    }


    @Test
    void create_ItemNotAvailable_ThrowsValidationException() {

        item.setAvailable(false);


        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        BookingDtoToPut bookingDtoToPut = new BookingDtoToPut(item.getId(), LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));

        when(itemService.getItemById(item.getId())).thenReturn(ItemMapper.mapToItemDtoWithComments(item));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            bookingService.create(bookingDtoToPut, user.getId());
        });

        assertEquals("Вещь с id = " + item.getId() + " не доступна для бронирования!",
                exception.getMessage());
    }

    @Test
    void create_ItemIsOwner_ThrowsNotFoundException() {
        Long bookerId = 1L;
        Long itemId = 2L;

        User booker = new User();
        booker.setId(bookerId);

        User owner = new User();
        owner.setId(bookerId);

        Item item = new Item();
        item.setId(itemId);
        item.setAvailable(true);
        item.setOwner(owner);

        BookingDtoToPut bookingDto = new BookingDtoToPut(itemId, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemService.getItemById(itemId)).thenReturn(ItemMapper.mapToItemDtoWithComments(item));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            bookingService.create(bookingDto, bookerId);
        });

        assertEquals("Пользователь не может бронировать собственные вещи!", exception.getMessage());
    }

    @Test
    void create_ShouldThrowNotFound_WhenUserDoesNotExist() {
        Long bookerId = 1L;
        BookingDtoToPut bookingDto = new BookingDtoToPut();

        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        when(userRepository.findById(bookerId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            bookingService.create(bookingDto, bookerId);
        });

        assertEquals("Пользователь с id = 1 не найден.", exception.getMessage());
    }


    @Test
    void create_BookingTimeIntersects_ThrowsInvalidRequestException() {
        User owner = new User();
        owner.setId(1L);

        User booker = new User();
        booker.setId(2L);

        Item item = new Item();
        item.setId(3L);
        item.setAvailable(true);
        item.setOwner(owner);

        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemService.getItemById(item.getId())).thenReturn(ItemMapper.mapToItemDtoWithComments(item));

        Booking existingBooking = new Booking(4L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3),
                item, owner, Status.WAITING);
        when(bookingRepository.findBookingByItemId(item.getId())).thenReturn(Collections.singletonList(existingBooking));

        BookingDtoToPut bookingDtoToPut = new BookingDtoToPut(item.getId(), LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(4));

        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            bookingService.create(bookingDtoToPut, booker.getId());
        });

        assertEquals("Вещь с id = " + item.getId() + " находится в аренде.", exception.getMessage());
    }

    @Test
    void getLastBooking_NoLastBooking_ThrowsNotFoundException() {

        when(bookingRepository.getLastBookingForItem(item.getId())).thenReturn(null);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            bookingService.getLastBooking(item.getId());
        });

        assertEquals("Не найдено последнее бронирование для вещи с id = " + item.getId(),
                exception.getMessage());
    }


    @Test
    void getNextBooking_NoNextBooking_ThrowsNotFoundException() {
        when(bookingRepository.getNextBookingForItem(item.getId())).thenReturn(null);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            bookingService.getNextBooking(item.getId());
        });

        assertEquals("Не найдено следующее бронирование для вещи с id = " + item.getId(),
                exception.getMessage());
    }

    @Test
    void getBookingsOfOwnerItems_NoItems_ThrowsNotFoundException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findByOwnerId(user.getId())).thenReturn(Collections.emptyList());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            bookingService.getBookingsOfOwnerItems(user.getId(), "ALL");
        });

        assertEquals("У пользователя c id = " + user.getId() + " нет вещей для бронирования.",
                exception.getMessage());
    }

    @Test
    void getBookingsOfOwnerItems_ValidRequestWithNoBookings_ReturnsEmptyList() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findByOwnerId(user.getId())).thenReturn(Collections.singletonList(item));
        when(bookingRepository.getAllBookingsForItems(anyList())).thenReturn(Collections.emptyList());

        List<BookingDto> result = bookingService.getBookingsOfOwnerItems(user.getId(), "ALL");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void update_UserNotFound_ThrowsInvalidRequestException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(InvalidRequestException.class, () -> bookingService.update(booking.getId(),
                user.getId(), true));
    }

    @Test
    void getBookingById_ValidRequest_ReturnsBookingDto() {
        when(userService.getUserById(user.getId())).thenReturn(UserMapper.mapToUserDto(user));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.getBookingById(booking.getId(), user.getId());

        assertNotNull(result);
    }

    @Test
    void getBookingById_BookingNotFound_ThrowsNotFoundException() {
        when(userService.getUserById(user.getId())).thenReturn(UserMapper.mapToUserDto(user));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(booking.getId(), user.getId()));
    }


    @Test
    void getLastBooking_ValidItemId_ReturnsLastBooking() {
        when(bookingRepository.getLastBookingForItem(item.getId())).thenReturn(booking);

        BookingDto result = bookingService.getLastBooking(item.getId());

        assertNotNull(result);
    }

    @Test
    void getNextBooking_ValidItemId_ReturnsNextBooking() {
        when(bookingRepository.getNextBookingForItem(item.getId())).thenReturn(booking);

        BookingDto result = bookingService.getNextBooking(item.getId());

        assertNotNull(result);
    }

    @Test
    void create_BookingWithDifferentStatus_ReturnsCorrectStatus() {
        Long bookerId = 1L;
        Long itemId = 2L;

        User booker = new User();
        booker.setId(bookerId);

        User itemOwner = new User();
        itemOwner.setId(3L);

        Item item = new Item();
        item.setId(itemId);
        item.setAvailable(true);
        item.setOwner(itemOwner);

        BookingDtoToPut bookingDto = new BookingDtoToPut(itemId, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemService.getItemById(itemId)).thenReturn(ItemMapper.mapToItemDtoWithComments(item));
        when(bookingRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        BookingDto result = bookingService.create(bookingDto, bookerId);

        assertEquals(Status.WAITING, result.getStatus());
    }

    @Test
    void create_InvalidTimeFrame_ThrowsInvalidRequestException() {
        Long bookerId = 1L;
        Long itemId = 2L;

        User booker = new User();
        booker.setId(bookerId);

        Item item = new Item();
        item.setId(itemId);
        item.setAvailable(true);
        item.setOwner(new User(3L, "Owner", "owner@example.com"));

        LocalDateTime existingStart = LocalDateTime.now();
        LocalDateTime existingEnd = LocalDateTime.now().plusDays(1);
        Booking existingBooking = new Booking();
        existingBooking.setItem(item);
        existingBooking.setStart(existingStart);
        existingBooking.setEnd(existingEnd);

        BookingDtoToPut bookingDto = new BookingDtoToPut(itemId, existingStart.plusHours(1), existingEnd.minusHours(1));

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemService.getItemById(itemId)).thenReturn(ItemMapper.mapToItemDtoWithComments(item));
        when(bookingRepository.findBookingByItemId(itemId)).thenReturn(List.of(existingBooking));

        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            bookingService.create(bookingDto, bookerId);
        });

        assertEquals("Вещь с id = 2 находится в аренде.", exception.getMessage());
    }


    @Test
    void getBookingsByUserIdWithState_NoBookings_ReturnsEmptyList() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.getAllByBookerId(user.getId())).thenReturn(Collections.emptyList());

        List<BookingDto> result = bookingService.getBookingsByUserIdWithState("ALL", user.getId());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void create_ShouldThrowNotFound_WhenUserTriesToBookOwnItem() {
        Long bookerId = 1L;
        Long itemId = 2L;

        // Создаем пользователя
        User user = new User();
        user.setId(bookerId);

        // Создаем предмет
        Item item = new Item();
        item.setId(itemId);
        item.setOwner(user);
        item.setAvailable(true); // Убедитесь, что здесь установлено значение

        // Настраиваем моки
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(user));
        when(itemService.getItemById(itemId)).thenReturn(ItemMapper.mapToItemDtoWithComments(item));

        // Создаем DTO для бронирования
        BookingDtoToPut bookingDto = new BookingDtoToPut();
        bookingDto.setItemId(itemId);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        // Проверка исключения
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            bookingService.create(bookingDto, bookerId);
        });

        assertEquals("Пользователь не может бронировать собственные вещи!", exception.getMessage());
    }

}