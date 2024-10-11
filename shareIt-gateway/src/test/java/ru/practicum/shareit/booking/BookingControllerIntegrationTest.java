package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerIntegrationTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BookingClient bookingClient;

    @SneakyThrows
    @Test
    void bookItem_whenValidRequest_thenReturnStatusIsOk() {
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        when(bookingClient.createBooking(any(BookingDto.class), any(Long.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void bookItem_whenValidRequest_thenReturnStatusIsCreated() {
        BookingDto requestDto = new BookingDto(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        String expectedStart = requestDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String expectedEnd = requestDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        when(bookingClient.createBooking(any(BookingDto.class), any(Long.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(requestDto));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.itemId").value(1L))
                .andExpect(jsonPath("$.start").value(expectedStart))
                .andExpect(jsonPath("$.end").value(expectedEnd));
    }

    @SneakyThrows
    @Test
    void bookItem_whenStartIsInThePast_thenReturnStatusIsBadRequest() {
        LocalDateTime start = LocalDateTime.now().minusDays(1).truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime end = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS);

        BookingDto requestDto = new BookingDto(1L, start, end);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Ошибка валидации"));

    }

    @SneakyThrows
    @Test
    void bookItem_whenEndIsBeforeStart_thenReturnStatusIsBadRequest() {
        BookingDto requestDto = new BookingDto(1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(0)
        );

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Ошибка валидации"))
                .andExpect(jsonPath("$.description").value("end: must be a future date"));
    }

    @SneakyThrows
    @Test
    void changeStatus_whenValidRequest_thenReturnStatusIsOk() {
        long bookingId = 1L;
        boolean approved = true;

        when(bookingClient.updateBooking(eq(bookingId), eq(1L), eq(approved)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", String.valueOf(approved)))
                .andDo(print())
                .andExpect(status().isOk());

        verify(bookingClient).updateBooking(eq(bookingId), eq(1L), eq(approved));
    }

    @SneakyThrows
    @Test
    void getBooking_whenValidRequest_thenReturnStatusIsOk() {
        long bookingId = 1L;
        long itemId = 3L; // Пример ID предмета
        LocalDateTime start = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime end = LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.SECONDS);


        BookingResponseDto bookingResponseDto = new BookingResponseDto(
                bookingId,
                itemId,
                start,
                end,
                true
        );


        when(bookingClient.getBookingById(any(Long.class), eq(bookingId)))
                .thenReturn(ResponseEntity.ok(bookingResponseDto));

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.itemId").value(itemId))
                .andExpect(jsonPath("$.start").value(start.toString()))
                .andExpect(jsonPath("$.end").value(end.toString()))
                .andExpect(jsonPath("$.approved").value(true));
    }
}