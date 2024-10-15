package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.server.booking.controller.BookingController;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.booking.dto.BookingDtoToPut;
import ru.practicum.shareit.server.booking.service.BookingService;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void addBooking_ValidRequest_ReturnsBookingDto() throws Exception {
        Long userId = 1L;
        BookingDtoToPut bookingDtoToPut = new BookingDtoToPut();
        BookingDto expectedBookingDto = new BookingDto();

        when(bookingService.create(any(), anyLong())).thenReturn(expectedBookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(bookingDtoToPut))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedBookingDto)));

        verify(bookingService).create(any(), eq(userId));
    }

    @Test
    void updateBooking_ValidRequest_ReturnsUpdatedBookingDto() throws Exception {
        Long userId = 1L;
        Long bookingId = 1L;
        BookingDto expectedBookingDto = new BookingDto();

        when(bookingService.update(anyLong(), anyLong(), anyBoolean())).thenReturn(expectedBookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedBookingDto)));

        verify(bookingService).update(eq(bookingId), eq(userId), eq(true));
    }

    @Test
    void getBooking_ValidRequest_ReturnsBookingDto() throws Exception {
        Long userId = 1L;
        Long bookingId = 1L;
        BookingDto expectedBookingDto = new BookingDto();

        when(bookingService.getBookingById(anyLong(), anyLong())).thenReturn(expectedBookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedBookingDto)));

        verify(bookingService).getBookingById(eq(bookingId), eq(userId));
    }

    @Test
    void getAllUserBookings_ValidUserId_ReturnsBookingDtoList() throws Exception {
        Long userId = 1L;
        List<BookingDto> expectedBookings = Collections.singletonList(new BookingDto());

        when(bookingService.getBookingsByUserIdWithState(any(), eq(userId))).thenReturn(expectedBookings);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedBookings)));

        verify(bookingService).getBookingsByUserIdWithState(any(), eq(userId));
    }

    @Test
    void getBookingsOfAllOwnersItemsByUserId_ValidUserId_ReturnsBookingDtoList() throws Exception {
        Long userId = 1L;
        List<BookingDto> expectedBookings = Collections.singletonList(new BookingDto());

        when(bookingService.getBookings(any(), eq(userId))).thenReturn(expectedBookings);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedBookings)));

        verify(bookingService).getBookings(any(), eq(userId));
    }
}
