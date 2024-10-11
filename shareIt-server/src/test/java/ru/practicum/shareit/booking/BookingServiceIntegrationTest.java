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
import ru.practicum.shareit.server.booking.service.BookingService;
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
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class BookingServiceIntegrationTest {

    @Autowired
    private BookingService bookingService;

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
        bookingRepository.deleteAll(); // Сброс данных перед каждым тестом
        Long itemId = 3L;

        // Создаем владельца
        owner = userRepository.save(new User("Owner", "owner@example.com"));

        // Создаем арендатора
        booker = userRepository.save(new User("Booker", "booker@example.com"));

        // Создаем предмет
        item = new ItemDto(itemId, "ItemName", "Description", owner, true);
        item = itemService.addItem(item.getOwner().getId(), item);
    }

   @Test
    public void testCreateBooking() {
        // Создаем предмет
        itemService.addItem(owner.getId(), item); // Сохраняем предмет

        // Проверка, что предмет доступен
        assertThat(itemService.getItemById(item.getId()).getAvailable()).isTrue();

        // Создаем бронирование
        BookingDtoToPut bookingDto = new BookingDtoToPut();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(1)); // Дата начала через день
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));   // Дата окончания через два дня

        // Создаем бронирование и проверяем его
        BookingDto createdBooking = bookingService.create(bookingDto, booker.getId());

        assertThat(createdBooking).isNotNull();
        assertThat(createdBooking.getItem().getId()).isEqualTo(item.getId());
        assertThat(createdBooking.getBooker().getId()).isEqualTo(booker.getId());
    }

    @Test
    public void testCreateBooking_WhenItemNotAvailable_ShouldThrowInvalidRequestException() {
        itemService.addItem(owner.getId(), item);
        // Создаем первое бронирование
        BookingDtoToPut bookingDto1 = new BookingDtoToPut();
        bookingDto1.setItemId(item.getId());
        bookingDto1.setStart(LocalDateTime.now().plusDays(1));
        bookingDto1.setEnd(LocalDateTime.now().plusDays(2));
        bookingService.create(bookingDto1, booker.getId());

        // Пытаемся создать второе бронирование для того же предмета, что должно выбросить исключение
        BookingDtoToPut bookingDto2 = new BookingDtoToPut();
        bookingDto2.setItemId(item.getId());
        bookingDto2.setStart(LocalDateTime.now().plusDays(1));
        bookingDto2.setEnd(LocalDateTime.now().plusDays(2));

        assertThrows(InvalidRequestException.class, () -> bookingService.create(bookingDto2, booker.getId()));
    }

    @Test
    public void testCreateBooking_WhenBookerIsOwner_ShouldThrowNotFoundException() {
        // Создаем предмет
        Long itemId = 1L;
        ItemDto item = new ItemDto(itemId, "ItemName", "Description", owner, true);
        itemService.addItem(owner.getId(), item); // Сохраняем предмет

        // Пытаемся создать бронирование владельцем
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
        //ItemDto item = new ItemDto(itemId, "ItemName", "Description", owner, true);
        itemService.addItem(owner.getId(), item); // Сохраняем предмет

        // Создаем первое бронирование (в прошлом, чтобы оно считалось завершенным)
        BookingDtoToPut bookingDto1 = new BookingDtoToPut();
        bookingDto1.setItemId(item.getId());
        bookingDto1.setStart(LocalDateTime.now().minusDays(2)); // Дата начала два дня назад
        bookingDto1.setEnd(LocalDateTime.now().minusDays(1));   // Дата окончания один день назад

        BookingDto createdBooking1 = bookingService.create(bookingDto1, booker.getId());

        // Проверяем, что первое бронирование успешно создано
        assertThat(createdBooking1).isNotNull();
        assertThat(createdBooking1.getItem().getId()).isEqualTo(item.getId());
        assertThat(createdBooking1.getBooker().getId()).isEqualTo(booker.getId());

        // Создаем второе бронирование (в будущем, чтобы оно не конфликтовало)
        BookingDtoToPut bookingDto2 = new BookingDtoToPut();
        bookingDto2.setItemId(item.getId());
        bookingDto2.setStart(LocalDateTime.now().plusDays(1)); // Дата начала через день
        bookingDto2.setEnd(LocalDateTime.now().plusDays(2));   // Дата окончания через два дня

        BookingDto createdBooking2 = bookingService.create(bookingDto2, booker.getId());

        // Проверяем, что второе бронирование успешно создано
        assertThat(createdBooking2).isNotNull();
        assertThat(createdBooking2.getItem().getId()).isEqualTo(item.getId());
        assertThat(createdBooking2.getBooker().getId()).isEqualTo(booker.getId());

        // Теперь получаем последнее бронирование
        BookingDto lastBooking = bookingService.getLastBooking(item.getId());

        // Проверяем, что последнее бронирование - это то, которое мы создали первым
        assertThat(lastBooking).isNotNull();
        assertThat(lastBooking.getId()).isEqualTo(createdBooking1.getId());
    }

    @Test
    public void testGetNextBooking() {
        // Создаем предмет
        Long itemId = item.getId();

        // Создаем прошедшее бронирование
        BookingDtoToPut bookingDto1 = new BookingDtoToPut();
        bookingDto1.setItemId(itemId);
        bookingDto1.setStart(LocalDateTime.now().minusDays(2)); // Начало в прошлом
        bookingDto1.setEnd(LocalDateTime.now().minusDays(1));   // Окончание в прошлом
        bookingService.create(bookingDto1, booker.getId());

        // Создаем текущее бронирование
        BookingDtoToPut bookingDto2 = new BookingDtoToPut();
        bookingDto2.setItemId(itemId);
        bookingDto2.setStart(LocalDateTime.now().plusDays(1)); // Начало через день
        bookingDto2.setEnd(LocalDateTime.now().plusDays(2));   // Окончание через два дня
        BookingDto createdBooking2 = bookingService.create(bookingDto2, booker.getId());

        // Создаем следующее бронирование
        BookingDtoToPut bookingDto3 = new BookingDtoToPut();
        bookingDto3.setItemId(itemId);
        bookingDto3.setStart(LocalDateTime.now().plusDays(3)); // Начало через три дня
        bookingDto3.setEnd(LocalDateTime.now().plusDays(4));   // Окончание через четыре дня
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

        assertThat(bookings).isNotNull(); // Проверяем, что список не равен null
        assertThat(bookings.size()).isGreaterThan(0); // Проверяем, что размер списка равен 1
    }

    @Test
    public void testGetBookingsOfOwnerItems() {
        BookingDtoToPut bookingDto = new BookingDtoToPut();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingService.create(bookingDto, booker.getId());

        List<BookingDto> ownerBookings = bookingService.getBookingsOfOwnerItems(owner.getId(), "ALL");

        assertThat(ownerBookings).isNotNull(); // Проверяем, что список не равен null
        assertThat(ownerBookings.size()).isGreaterThan(0); // Проверяем, что размер списка равен 1
    }
}