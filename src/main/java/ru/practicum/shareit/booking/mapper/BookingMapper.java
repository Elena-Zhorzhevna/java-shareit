package ru.practicum.shareit.booking.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoToPut;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserServiceImpl;

/**
 * Класс для преобразования объектов типа Booking в тип BookingDto и обратно.
 */
@Component
public class BookingMapper {
    private final UserServiceImpl userService;
    private final ItemServiceImpl itemService;
    private final ItemMapper itemMapper;

    @Autowired
    public BookingMapper(UserServiceImpl userService, ItemServiceImpl itemService, ItemMapper itemMapper) {
        this.userService = userService;
        this.itemService = itemService;
        this.itemMapper = itemMapper;
    }

    public static Booking mapToBooking(BookingDto dto) {
        Booking booking = new Booking();
        booking.setItem(ItemMapper.mapItemDtoToItem(dto.getItem()));
        booking.setBooker(UserMapper.mapUserDtoToUser(dto.getBooker()));
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());
        booking.setStatus(dto.getStatus());
        return booking;
    }

    public BookingDto mapToBookingDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setItem(itemMapper.mapToItemDtoWithComments(booking.getItem()));
        dto.setBooker(UserMapper.mapToUserDto(booking.getBooker()));
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setStatus(booking.getStatus());
        return dto;
    }

    public Booking mapBookingDtoToPutToBooking(BookingDtoToPut bookingDtoToPut, Long bookerId) {
        return new Booking(
                null,
                bookingDtoToPut.getStart(),
                bookingDtoToPut.getEnd(),
                ItemMapper.mapItemDtoToItem(itemService.getItemById(bookingDtoToPut.getItemId())),
                UserMapper.mapUserDtoToUser(userService.getUserById(bookerId)),
                Status.WAITING
        );
    }
}