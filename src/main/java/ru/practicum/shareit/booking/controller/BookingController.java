package ru.practicum.shareit.booking.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoToPut;

import java.util.List;

/**
 * Класс контроллера для управления бронированием вещей в приложении ShareIt.
 */
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private static final String USER_ID_REQUEST_HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto addBooking(@RequestHeader(value = USER_ID_REQUEST_HEADER) Long userId,
                                 @RequestBody BookingDtoToPut bookingDto) {
        log.info("Добавление нового запроса на бронирование пользователем с id = " + userId);
        return bookingService.create(bookingDto, userId);
    }

    /**
     * Подтверждение или отклонение запроса.
     *
     * @param userId    Идентификатор владельца вещи.
     * @param bookingId Идентификатор бронирования.
     * @param approved  Параметр принимает значения true или false
     * @return Бронирование с обновленным статусом бронирования в формате Dto.
     */
    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@RequestHeader(value = USER_ID_REQUEST_HEADER) Long userId,
                                    @PathVariable Long bookingId,
                                    @RequestParam Boolean approved) {
        log.info("Запрос на изменение статуса бронирования id = " + bookingId + " от пользователя id = " + userId);
        return bookingService.update(bookingId, userId, approved);
    }

    /**
     * Получение данных о конкретном бронировании, включая его статус. Может быть выполнено либо автором бронирования,
     * либо владельцем вещи, к которой относится бронирование.
     *
     * @param userId    Идентификатор пользователя.
     * @param bookingId Идентификатор бронирования.
     * @return
     */
    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader(value = USER_ID_REQUEST_HEADER) Long userId,
                                 @PathVariable Long bookingId) {
        log.info("Получение информации о бронировании с id = " + bookingId);
        return bookingService.getBookingById(bookingId, userId);
    }

    /**
     * Получение всех бронирований пользователя с указанным идентификатором.
     *
     * @param userId Идентификатор пользователя.
     * @param state  Параметр state необязательный, умолчанию равен ALL.
     *               Также он может принимать значения CURRENT (англ. «текущие»)
     *               PAST (англ. «завершённые»)
     *               FUTURE (англ. «будущие»)
     *               WAITING (англ. «ожидающие подтверждения»)
     *               REJECTED (англ. «отклонённые»)
     * @return Список бронирований в формате Дто.
     */
    @GetMapping()
    public List<BookingDto> getAllUserBookings(@RequestHeader(value = USER_ID_REQUEST_HEADER) Long userId,
                                               @RequestParam(required = false) String state) {
        log.info("Запрос на получение данных о бронировании пользователя с id = " + userId);
        return bookingService.getBookingsByUserIdWithState(state, userId);
    }

    /**
     * Получение списка бронирований для всех вещей текущего пользователя.
     *
     * @param userId Идентификатор пользователя.
     * @param state  Параметр state необязательный, умолчанию равен ALL.
     *               Также он может принимать значения
     *               CURRENT (англ. «текущие»)
     *               PAST (англ. «завершённые»)
     *               FUTURE (англ. «будущие»)
     *               WAITING (англ. «ожидающие подтверждения»)
     *               REJECTED (англ. «отклонённые»)
     * @return Список бронирований всех вещей пользователя с указанным идентификатором.
     */
    @GetMapping("/owner")
    public List<BookingDto> getBookingsOfAllOwnersItemsByUserId(@RequestHeader(value = USER_ID_REQUEST_HEADER)
                                                                Long userId, @RequestParam(required = false)
                                                                String state) {
        log.info("Получение бронирований всех вещей пользователя с id = " + userId);
        return bookingService.getBookings(state, userId);
    }
}