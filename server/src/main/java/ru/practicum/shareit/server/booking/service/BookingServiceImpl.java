package ru.practicum.shareit.server.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.server.booking.mapper.BookingMapper;
import ru.practicum.shareit.server.booking.storage.BookingRepository;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.booking.dto.BookingDtoToPut;
import ru.practicum.shareit.server.booking.model.Booking;
import ru.practicum.shareit.server.booking.model.Status;
import ru.practicum.shareit.server.exception.InvalidRequestException;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.exception.ValidationException;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.mapper.ItemMapper;
import ru.practicum.shareit.server.item.storage.ItemRepository;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.item.service.ItemService;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.storage.UserRepository;
import ru.practicum.shareit.server.user.model.User;
import ru.practicum.shareit.server.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserServiceImpl userService;
    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, UserServiceImpl userService,
                              ItemService itemService, ItemRepository itemRepository,
                              UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.userService = userService;
        this.itemService = itemService;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }


    @Override
    public BookingDto create(BookingDtoToPut bookingDtoToPut, Long bookerId) {

        if (bookingDtoToPut.getEnd().isBefore(bookingDtoToPut.getStart())) {
            throw new InvalidRequestException("Дата окончания должна быть позже даты начала");
        }

        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + bookerId + " не найден."));

        if (bookingDtoToPut.getItemId() == null) {
            throw new ValidationException("Данные для бронирования некорректны.");
        }

        ItemDto itemDto = itemService.getItemById(bookingDtoToPut.getItemId());
        if (itemDto == null) {
            throw new NotFoundException("Предмет с id = " + bookingDtoToPut.getItemId() + " не найден.");
        }

        Item item = ItemMapper.mapItemDtoToItem(itemDto);

        if (!item.getAvailable()) {
            throw new ValidationException("Вещь с id = " + bookingDtoToPut.getItemId() + " не доступна для бронирования!");
        }

        if (item.getOwner().getId().equals(bookerId)) {
            throw new NotFoundException("Пользователь не может бронировать собственные вещи!");
        }

        timeIntersectionsCheck(bookingDtoToPut, item.getId());

        Booking bookingToCreate = BookingMapper.mapBookingDtoToPutToBooking(bookingDtoToPut);
        bookingToCreate.setItem(item);
        bookingToCreate.setBooker(booker);

        Booking savedBooking = bookingRepository.save(bookingToCreate);
        return BookingMapper.mapToBookingDto(savedBooking);
    }

    /**
     * Обновление бронирования.
     *
     * @param bookingId Идентификатор бронирования.
     * @param userId    Идентификатор пользователя.
     * @param approved  Параметр показывающий, подтверждено ли бронирование.
     * @return Бронирование с обновленными данными в формате Дто.
     */

    public BookingDto update(Long bookingId, Long userId, Boolean approved) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new InvalidRequestException("Пользователь с id = " + userId + " не найден."));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено."));

        isItemOwner(booking.getItem().getId(), user.getId());

        setBookingStatus(booking, approved);
        bookingRepository.save(booking);

        return BookingMapper.mapToBookingDto(booking);
    }

    /**
     * Получение бронирования по идентификаторам пользователя и бронирования.
     *
     * @param bookingId Идентификатор бронирования.
     * @param userId    Идентификатор пользователя.
     * @return Бронирование с указанным идентификатором.
     */

    @Override
    public BookingDto getBookingById(Long bookingId, Long userId) {
        UserDto userDto = userService.getUserById(userId);
        if (userDto == null) {
            throw new NotFoundException("Пользователя с id = " + userId + " не существует.");
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id = " + bookingId + " не найдено!"));

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Данные бронирования доступны только для владельца вещи или арендатора.");
        }

        return BookingMapper.mapToBookingDto(booking);
    }


    /**
     * @param state  Параметр state необязательный и по умолчанию равен ALL (англ. «все»).
     *               Также он может принимать значения CURRENT (англ. «текущие»),
     *               PAST (англ. «завершённые»),
     *               FUTURE (англ. «будущие»),
     *               WAITING (англ. «ожидающие подтверждения»),
     *               REJECTED (англ. «отклонённые»).
     * @param userId Идентификатор пользователя.
     * @return Список бронирований указанного пользователя с указанным параметром state.
     */
    @Override
    public List<BookingDto> getBookingsByUserIdWithState(String state, Long userId) {
        if (state == null) {
            state = "ALL";
        }
        userService.getUserById(userId);
        List<BookingDto> bookings = getBookings(state, userId);
        return new ArrayList<>(bookings);
    }

    /**
     * Получение списка всех бронирований текущего пользователя.
     *
     * @param state  Параметр state необязательный и по умолчанию равен ALL (англ. «все»).
     *               Также он может принимать значения CURRENT (англ. «текущие»),
     *               PAST (англ. «завершённые»),
     *               FUTURE (англ. «будущие»),
     *               WAITING (англ. «ожидающие подтверждения»),
     *               REJECTED (англ. «отклонённые»).
     * @param userId Идентификатор пользователя.
     * @return Список бронирований в формате Дто.
     */
    @Override
    public List<BookingDto> getBookings(String state, Long userId) {
        isUserExist(userId);
        List<Booking> bookings;
        bookings = switch (state) {
            case "ALL" -> bookingRepository.getAllByBookerId(userId);
            case "CURRENT" -> bookingRepository.getAllCurrentByUserId(userId);
            case "PAST" -> bookingRepository.getAllPastByUserId(userId);
            case "FUTURE" -> bookingRepository.getAllFutureByUserId(userId);
            case "WAITING" -> bookingRepository.getAllWaitingByUserId(userId);
            case "REJECTED" -> bookingRepository.getAllRejectedByUserId(userId);
            default -> throw new NotFoundException("Не найден параметр " + state);
        };
        return bookings.stream()
                .map(BookingMapper::mapToBookingDto)
                .collect(Collectors.toList());
    }

    /**
     * Получение последнего бронирования вещи.
     *
     * @param itemId Идентификатор вещи.
     * @return Бронирование в формате Дто.
     */

    @Override
    public BookingDto getLastBooking(Long itemId) {
        Booking lastBooking = bookingRepository.getLastBookingForItem(itemId);
        if (lastBooking == null) {
            throw new NotFoundException("Не найдено последнее бронирование для вещи с id = " + itemId);
        }
        return BookingMapper.mapToBookingDto(lastBooking);
    }

    /**
     * Получение следующего бронирования вещи.
     *
     * @param itemId Идентификатор вещи.
     * @return Бронирование в формате Дто.
     */
    @Override
    public BookingDto getNextBooking(Long itemId) {
        Booking nextBooking = bookingRepository.getNextBookingForItem(itemId);
        if (nextBooking == null) {
            throw new NotFoundException("Не найдено следующее бронирование для вещи с id = " + itemId);
        }
        return BookingMapper.mapToBookingDto(nextBooking);
    }

    /**
     * Получение бронирований всех вещей владельца.
     *
     * @param ownerId Идентификатор бронирования.
     * @param state   Параметр state необязательный и по умолчанию равен ALL (англ. «все»).
     *                Также он может принимать значения CURRENT (англ. «текущие»),
     *                PAST (англ. «завершённые»),
     *                FUTURE (англ. «будущие»),
     *                WAITING (англ. «ожидающие подтверждения»),
     *                REJECTED (англ. «отклонённые»).
     * @return Список бронирований вещей владельца.
     */
    public List<BookingDto> getBookingsOfOwnerItems(Long ownerId, String state) {
        isUserExist(ownerId);
        List<Long> itemIds = itemRepository.findByOwnerId(ownerId).stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        if (itemIds.isEmpty()) {
            throw new NotFoundException("У пользователя c id = " + ownerId + " нет вещей для бронирования.");
        }
        List<Booking> bookings = findBookingsOfItemsWithState(itemIds, state);
        return bookings.stream()
                .map(BookingMapper::mapToBookingDto)
                .collect(Collectors.toList());
    }

    /**
     * Метод проверяет, является ли пользователь владельцем вещи.
     *
     * @param itemId Идентификатор вещи.
     * @return true or false
     */

    private void isItemOwner(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователь не является владельцем этого предмета");
        }
    }

    /**
     * Метод проверяет доступность вещи.
     *
     * @param itemId Идентификатор вещи.
     * @return true or false
     */
    private boolean isAvailableItem(Long itemId) {
        return itemService.getItemById(itemId).getAvailable();
    }

    /**
     * Метод проверяет существование пользователя.
     *
     * @param userId Идентификатор пользователя.
     */
    private User isUserExist(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id = " + userId + " не найден.")
        );
        return user;
    }

    /**
     * Получение бронирований вещей.
     *
     * @param itemIds Список идентификаторов вещей.
     * @param state   Параметр state необязательный и по умолчанию равен ALL (англ. «все»).
     *                Также он может принимать значения CURRENT (англ. «текущие»),
     *                PAST (англ. «завершённые»),
     *                FUTURE (англ. «будущие»),
     *                WAITING (англ. «ожидающие подтверждения»),
     *                REJECTED (англ. «отклонённые»).
     * @return Список бронирований.
     */
    private List<Booking> findBookingsOfItemsWithState(List<Long> itemIds, String state) {
        if (state == null) {
            state = "ALL";
        }
        return switch (state.toUpperCase()) {
            case "ALL" -> bookingRepository.getAllBookingsForItems(itemIds);
            case "CURRENT" -> bookingRepository.getCurrentBookingsForItems(itemIds);
            case "PAST" -> bookingRepository.getPastBookingsForItems(itemIds);
            case "FUTURE" -> bookingRepository.getFutureBookingsForItems(itemIds);
            case "WAITING" -> bookingRepository.getWaitingBookingsForItems(itemIds);
            case "REJECTED" -> bookingRepository.getRejectedBookingsForItems(itemIds);
            case "CANCELED" -> bookingRepository.getCanceledBookingsForItems(itemIds);
            default -> throw new NotFoundException("Неизвестный параметр state.");
        };
    }

    /**
     * Метод проверяет наличие статуса бронирования определенного пользователя.
     *
     * @param booking  Бронирование для проверки статуса.
     * @param approved Параметр, указывающий, подтверждено ли бронирование владельцем.
     */
    private void setBookingStatus(Booking booking, Boolean approved) {
        if (Status.APPROVED.equals(booking.getStatus())) {
            throw new ValidationException("Статус аренды уже подтвержден");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
    }

    private void timeIntersectionsCheck(BookingDtoToPut bookingDtoToPut, Long itemId) {
        final List<Booking> bookings = bookingRepository.findBookingByItemId(itemId);
        if (bookings.stream()
                .anyMatch(booking -> (!bookingDtoToPut.getStart().isAfter(booking.getEnd())
                        && !bookingDtoToPut.getEnd().isBefore(booking.getStart())))) {
            log.warn("Вещь с id {} находится в аренде.", itemId);
            throw new InvalidRequestException(String.format("Вещь с id = %d находится в аренде.", itemId));
        }
    }
}