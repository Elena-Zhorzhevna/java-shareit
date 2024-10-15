package ru.practicum.shareit.booking.dto;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.booking.dto.BookingDtoToPut;
import ru.practicum.shareit.server.booking.mapper.BookingMapper;
import ru.practicum.shareit.server.booking.model.Booking;
import ru.practicum.shareit.server.booking.model.Status;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.mapper.ItemMapper;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.mapper.UserMapper;
import ru.practicum.shareit.server.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingMapperTest {

    @Test
    void mapToBooking_ShouldMapFieldsCorrectly() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusHours(1));
        bookingDto.setStatus(Status.WAITING);
        bookingDto.setItem(new ItemDto());
        bookingDto.setBooker(new UserDto());

        Booking booking = BookingMapper.mapToBooking(bookingDto);

        assertEquals(bookingDto.getStart(), booking.getStart());
        assertEquals(bookingDto.getEnd(), booking.getEnd());
        assertEquals(bookingDto.getStatus(), booking.getStatus());
        assertEquals(bookingDto.getItem(), ItemMapper.mapToItemDtoWithComments(booking.getItem()));
        assertEquals(bookingDto.getBooker(), UserMapper.mapToUserDto(booking.getBooker()));
    }

    @Test
    void mapToBookingDto_ShouldMapFieldsCorrectly() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusHours(1));
        booking.setStatus(Status.WAITING);
        booking.setItem(new Item());
        booking.setBooker(new User());

        BookingDto bookingDto = BookingMapper.mapToBookingDto(booking);

        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getStart(), bookingDto.getStart());
        assertEquals(booking.getEnd(), bookingDto.getEnd());
        assertEquals(booking.getStatus(), bookingDto.getStatus());
        assertEquals(booking.getItem(), ItemMapper.mapItemDtoToItem(bookingDto.getItem()));
        assertEquals(booking.getBooker(), UserMapper.mapUserDtoToUser(bookingDto.getBooker()));

    }

    @Test
    void mapBookingDtoToPutToBooking_ShouldSetFieldsCorrectly() {
        BookingDtoToPut bookingDtoToPut = new BookingDtoToPut();
        bookingDtoToPut.setStart(LocalDateTime.now());
        bookingDtoToPut.setEnd(LocalDateTime.now().plusHours(1));

        Booking booking = BookingMapper.mapBookingDtoToPutToBooking(bookingDtoToPut);

        assertEquals(bookingDtoToPut.getStart(), booking.getStart());
        assertEquals(bookingDtoToPut.getEnd(), booking.getEnd());
        assertEquals(Status.WAITING, booking.getStatus());
    }
}