package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoToPut;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

/**
 * Класс для преобразования объектов типа Booking в тип BookingDto и обратно.
 */
public class BookingMapper {

    public static Booking mapToBooking(BookingDto dto) {
        Booking booking = new Booking();
        booking.setItem(ItemMapper.mapItemDtoToItem(dto.getItem()));
        booking.setBooker(UserMapper.mapUserDtoToUser(dto.getBooker()));
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());
        booking.setStatus(dto.getStatus());
        return booking;
    }

    public static BookingDto mapToBookingDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setItem(ItemMapper.mapToItemDtoWithComments(booking.getItem()));
        dto.setBooker(UserMapper.mapToUserDto(booking.getBooker()));
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setStatus(booking.getStatus());
        return dto;
    }

    public static Booking mapBookingDtoToPutToBooking(BookingDtoToPut bookingDtoToPut) {
        return new Booking(
                null,
                bookingDtoToPut.getStart(),
                bookingDtoToPut.getEnd(),
                null,
                null,
                Status.WAITING
        );
    }
}