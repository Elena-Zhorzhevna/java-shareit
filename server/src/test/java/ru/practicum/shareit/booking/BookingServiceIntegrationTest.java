package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.booking.dto.BookingDtoToPut;

import ru.practicum.shareit.server.booking.model.Status;
import ru.practicum.shareit.server.booking.service.BookingServiceImpl;
import ru.practicum.shareit.server.booking.storage.BookingRepository;
import ru.practicum.shareit.server.exception.InvalidRequestException;
import ru.practicum.shareit.server.exception.NotFoundException;

import ru.practicum.shareit.server.item.dto.ItemDto;

import ru.practicum.shareit.server.item.service.ItemService;

import ru.practicum.shareit.server.item.storage.ItemRepository;
import ru.practicum.shareit.server.user.model.User;
import ru.practicum.shareit.server.user.service.UserService;
import ru.practicum.shareit.server.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import static org.junit.jupiter.api.Assertions.*;



@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class BookingServiceIntegrationTest {

    @Autowired
    private BookingServiceImpl bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User owner;
    private User booker;
    private ItemDto item;
    @Autowired
    private BookingRepository bookingRepository;


    @BeforeEach
    public void setUp() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
        bookingRepository.deleteAll();
        Long itemId = 3L;

        owner = userRepository.save(new User("Owner", "owner@example.com"));

        booker = userRepository.save(new User("Booker", "booker@example.com"));

        item = new ItemDto(itemId, "ItemName", "Description", owner, true);
        item = itemService.addItem(item.getOwner().getId(), item);
    }

    @Test
    public void testCreateBooking() {
        itemService.addItem(owner.getId(), item);

        assertThat(itemService.getItemById(item.getId()).getAvailable()).isTrue();

        BookingDtoToPut bookingDto = new BookingDtoToPut();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto createdBooking = bookingService.create(bookingDto, booker.getId());

        assertThat(createdBooking).isNotNull();
        assertThat(createdBooking.getItem().getId()).isEqualTo(item.getId());
        assertThat(createdBooking.getBooker().getId()).isEqualTo(booker.getId());
    }

    @Test
    public void testCreateBooking_WhenItemNotAvailable_ShouldThrowInvalidRequestException() {
        itemService.addItem(owner.getId(), item);

        BookingDtoToPut bookingDto1 = new BookingDtoToPut();
        bookingDto1.setItemId(item.getId());
        bookingDto1.setStart(LocalDateTime.now().plusDays(1));
        bookingDto1.setEnd(LocalDateTime.now().plusDays(2));
        bookingService.create(bookingDto1, booker.getId());

        BookingDtoToPut bookingDto2 = new BookingDtoToPut();
        bookingDto2.setItemId(item.getId());
        bookingDto2.setStart(LocalDateTime.now().plusDays(1));
        bookingDto2.setEnd(LocalDateTime.now().plusDays(2));

        assertThrows(InvalidRequestException.class, () -> bookingService.create(bookingDto2, booker.getId()));
    }

    @Test
    public void testCreateBooking_WhenBookerIsOwner_ShouldThrowNotFoundException() {

        Long itemId = 1L;
        ItemDto item = new ItemDto(itemId, "ItemName", "Description", owner, true);
        itemService.addItem(owner.getId(), item);

        BookingDtoToPut bookingDto = new BookingDtoToPut();
        bookingDto.setItemId(itemId);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        assertThrows(NotFoundException.class, () -> bookingService.create(bookingDto, owner.getId()));
    }

    @Test
    public void testGetBookingById() {
        BookingDtoToPut bookingDto = new BookingDtoToPut();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto createdBooking = bookingService.create(bookingDto, booker.getId());

        BookingDto foundBooking = bookingService.getBookingById(createdBooking.getId(), booker.getId());

        assertThat(foundBooking).isNotNull();
        assertThat(foundBooking.getId()).isEqualTo(createdBooking.getId());
    }

    @Test
    public void testUpdateBooking() {
        BookingDtoToPut bookingDto = new BookingDtoToPut();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto createdBooking = bookingService.create(bookingDto, booker.getId());

        BookingDto updatedBooking = bookingService.update(createdBooking.getId(), owner.getId(), true);

        assertThat(updatedBooking).isNotNull();
        assertThat(updatedBooking.getId()).isEqualTo(createdBooking.getId());
        assertThat(updatedBooking.getStatus()).isEqualTo(Status.APPROVED);
    }

    @Test
    public void testGetLastBooking() {
        itemService.addItem(owner.getId(), item); // Сохраняем предмет

        BookingDtoToPut bookingDto1 = new BookingDtoToPut();
        bookingDto1.setItemId(item.getId());
        bookingDto1.setStart(LocalDateTime.now().minusDays(2));
        bookingDto1.setEnd(LocalDateTime.now().minusDays(1));

        BookingDto createdBooking1 = bookingService.create(bookingDto1, booker.getId());

        assertThat(createdBooking1).isNotNull();
        assertThat(createdBooking1.getItem().getId()).isEqualTo(item.getId());
        assertThat(createdBooking1.getBooker().getId()).isEqualTo(booker.getId());

        BookingDtoToPut bookingDto2 = new BookingDtoToPut();
        bookingDto2.setItemId(item.getId());
        bookingDto2.setStart(LocalDateTime.now().plusDays(1));
        bookingDto2.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto createdBooking2 = bookingService.create(bookingDto2, booker.getId());

        assertThat(createdBooking2).isNotNull();
        assertThat(createdBooking2.getItem().getId()).isEqualTo(item.getId());
        assertThat(createdBooking2.getBooker().getId()).isEqualTo(booker.getId());

        BookingDto lastBooking = bookingService.getLastBooking(item.getId());

        assertThat(lastBooking).isNotNull();
        assertThat(lastBooking.getId()).isEqualTo(createdBooking1.getId());
    }

    @Test
    public void testGetNextBooking() {
        Long itemId = item.getId();

        BookingDtoToPut bookingDto1 = new BookingDtoToPut();
        bookingDto1.setItemId(itemId);
        bookingDto1.setStart(LocalDateTime.now().minusDays(2));
        bookingDto1.setEnd(LocalDateTime.now().minusDays(1));
        bookingService.create(bookingDto1, booker.getId());

        BookingDtoToPut bookingDto2 = new BookingDtoToPut();
        bookingDto2.setItemId(itemId);
        bookingDto2.setStart(LocalDateTime.now().plusDays(1));
        bookingDto2.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto createdBooking2 = bookingService.create(bookingDto2, booker.getId());

        BookingDtoToPut bookingDto3 = new BookingDtoToPut();
        bookingDto3.setItemId(itemId);
        bookingDto3.setStart(LocalDateTime.now().plusDays(3));
        bookingDto3.setEnd(LocalDateTime.now().plusDays(4));
        BookingDto createdBooking3 = bookingService.create(bookingDto3, booker.getId());

        BookingDto nextBooking = bookingService.getNextBooking(itemId);

        assertThat(nextBooking).isNotNull();
        assertThat(nextBooking.getId()).isEqualTo(createdBooking2.getId());
    }

    @Test
    public void testGetBookingsByUserId() {
        BookingDtoToPut bookingDto = new BookingDtoToPut();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingService.create(bookingDto, booker.getId());

        List<BookingDto> bookings = bookingService.getBookingsByUserIdWithState("ALL", booker.getId());

        assertThat(bookings).isNotNull();
        assertThat(bookings.size()).isGreaterThan(0);
    }

    @Test
    public void testGetBookingsOfOwnerItems() {
        BookingDtoToPut bookingDto = new BookingDtoToPut();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingService.create(bookingDto, booker.getId());

        List<BookingDto> ownerBookings = bookingService.getBookingsOfOwnerItems(owner.getId(), "ALL");

        assertThat(ownerBookings).isNotNull();
        assertThat(ownerBookings.size()).isGreaterThan(0);
    }

    @Test
    public void testUpdateBooking_WhenBookingNotFound_ShouldThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> {
            bookingService.update(999L, owner.getId(), true);
        });
    }

    @Test
    public void testGetBookingById_WhenUserNotAuthorized_ShouldThrowNotFoundException() {
        BookingDtoToPut bookingDto = new BookingDtoToPut();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto createdBooking = bookingService.create(bookingDto, booker.getId());

        assertThrows(NotFoundException.class, () -> {
            bookingService.getBookingById(createdBooking.getId(), 999L);
        });
    }

    @Test
    public void testGetBookingsByUserId_WhenUserHasNoBookings_ShouldReturnEmptyList() {
        List<BookingDto> bookings = bookingService.getBookingsByUserIdWithState("ALL", booker.getId());

        assertThat(bookings).isNotNull();
        assertThat(bookings.size()).isEqualTo(0);
    }

    @Test
    public void testGetBookingsOfOwnerItems_WhenNoBookings_ShouldReturnEmptyList() {
        List<BookingDto> ownerBookings = bookingService.getBookingsOfOwnerItems(owner.getId(), "ALL");

        assertThat(ownerBookings).isNotNull();
        assertThat(ownerBookings.size()).isEqualTo(0);
    }

    @Test
    public void testUpdateBooking_WhenNotOwner_ShouldThrowNotFoundException() {
        BookingDtoToPut bookingDto = new BookingDtoToPut();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto createdBooking = bookingService.create(bookingDto, booker.getId());

        assertThrows(NotFoundException.class, () -> {
            bookingService.update(createdBooking.getId(), booker.getId(), true);
        });
    }

    @Test
    public void testCreateBooking_WhenEndDateBeforeStartDate_ShouldThrowInvalidRequestException() {
        BookingDtoToPut bookingDto = new BookingDtoToPut();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(2));
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));

        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> bookingService.create(bookingDto, booker.getId()));

        assertThat(exception.getMessage()).isEqualTo("Дата окончания должна быть позже даты начала");
    }
}
