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
        // Мокаем поведение userRepository
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user)); // Возвращаем пользователя
        when(userService.getUserById(user.getId())).thenReturn(UserMapper.mapToUserDto(user)); // Возвращаем UserDto
        when(bookingRepository.getAllByBookerId(user.getId())).thenReturn(Collections.singletonList(booking)); // Возвращаем бронирование

        List<BookingDto> result = bookingService.getBookingsByUserIdWithState("ALL", user.getId());

        assertEquals(1, result.size());
    }

    @Test
    void getBookingsOfOwnerItems_ValidRequest_ReturnsBookingsList() {
        // Мокаем поведение userRepository
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // Мокаем предмет, который принадлежит пользователю
        when(itemRepository.findByOwnerId(user.getId())).thenReturn(Collections.singletonList(item));

        // Мокаем поведение bookingRepository для возврата списка бронирований
        when(bookingRepository.getAllBookingsForItems(Collections.singletonList(item.getId())))
                .thenReturn(Collections.singletonList(booking));

        // Вызов метода для получения бронирований владельца
        List<BookingDto> result = bookingService.getBookingsOfOwnerItems(user.getId(), "ALL");

        // Проверка, что размер результата соответствует ожиданиям
        assertEquals(1, result.size());
    }

    @Test
    void create_ItemNotAvailable_ThrowsValidationException() {
        // Устанавливаем, что предмет не доступен
        item.setAvailable(false);

        // Мокаем поведение userRepository, чтобы вернуть пользователя
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user)); // Возвращаем пользователя

        // Создаем BookingDtoToPut с корректными значениями
        BookingDtoToPut bookingDtoToPut = new BookingDtoToPut(item.getId(), LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));

        // Мокаем поведение itemService, чтобы вернуть ItemDto с недоступным предметом
        when(itemService.getItemById(item.getId())).thenReturn(ItemMapper.mapToItemDtoWithComments(item));

        // Проверка, что при создании бронирования выбрасывается ValidationException
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            bookingService.create(bookingDtoToPut, user.getId());
        });

        // Дополнительно проверяем сообщение исключения
        assertEquals("Вещь с id = " + item.getId() + " не доступна для бронирования!",
                exception.getMessage());
    }

    @Test
    void getLastBooking_NoLastBooking_ThrowsNotFoundException() {
        // Настройка мока, чтобы вернуть null
        when(bookingRepository.getLastBookingForItem(item.getId())).thenReturn(null);

        // Проверка, что метод выбрасывает NotFoundException
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            bookingService.getLastBooking(item.getId());
        });

        // Проверка сообщения исключения
        assertEquals("Не найдено последнее бронирование для вещи с id = " + item.getId(),
                exception.getMessage());
    }


    @Test
    void getNextBooking_NoNextBooking_ThrowsNotFoundException() {
        when(bookingRepository.getNextBookingForItem(item.getId())).thenReturn(null);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            bookingService.getNextBooking(item.getId());
        });

        assertEquals("Не найдено следующее бронирование для вещи с id = " + item.getId(), exception.getMessage());
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
}
