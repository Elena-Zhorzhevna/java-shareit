package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RestClientTest(BookingClient.class)
class BookingClientTest {
    private final String serverUrl = "http://localhost:9090/bookings";

    @Autowired
    private BookingClient bookingClient;

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private ObjectMapper objectMapper;

    private BookingDto bookingDto;
    private String bookingBody;

    @BeforeEach
    @SneakyThrows
    void setUp() {
        bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();
        bookingBody = objectMapper.writeValueAsString(bookingDto);
    }

    @Test
    @SneakyThrows
    void getBookings() {
        Long userId = 1L;

        mockServer.expect(requestTo(serverUrl + "?state=ALL&from=0&size=10"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .body("[]") // пример ответа
                        .contentType(MediaType.APPLICATION_JSON));

        ResponseEntity<Object> responseEntity = bookingClient.getBookings(userId, BookingState.ALL, 0, 10);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }

    @Test
    @SneakyThrows
    void createBooking() {
        Long userId = 1L;

        mockServer.expect(requestTo(serverUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.CREATED)
                        .body(bookingBody)
                        .contentType(MediaType.APPLICATION_JSON));

        ResponseEntity<Object> responseEntity = bookingClient.createBooking(bookingDto, userId);
        String responseBody = objectMapper.writeValueAsString(responseEntity.getBody());

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(responseBody, bookingBody);
    }

    @Test
    @SneakyThrows
    void updateBooking() {
        Long bookingId = 1L;
        Long userId = 1L;

        mockServer.expect(requestTo(serverUrl + "/" + bookingId + "?approved=true"))
                .andExpect(method(HttpMethod.PATCH))
                .andRespond(withStatus(HttpStatus.OK)
                        .body(bookingBody)
                        .contentType(MediaType.APPLICATION_JSON));

        ResponseEntity<Object> responseEntity = bookingClient.updateBooking(bookingId, userId, true);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    @SneakyThrows
    void getBookingById() {
        Long bookingId = 1L;
        Long userId = 1L;

        mockServer.expect(requestTo(serverUrl + "/" + bookingId))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .body(bookingBody)
                        .contentType(MediaType.APPLICATION_JSON));

        ResponseEntity<Object> responseEntity = bookingClient.getBookingById(userId, bookingId);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    @SneakyThrows
    void getAllBookingsByOwner() {
        Long userId = 1L;

        mockServer.expect(requestTo(serverUrl + "/owner?state=ALL"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .body("[]") // пример ответа
                        .contentType(MediaType.APPLICATION_JSON));

        ResponseEntity<Object> responseEntity = bookingClient.getAllBookingsByOwner(userId, BookingState.ALL);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    // Тесты на обработку ошибок

    @Test
    @SneakyThrows
    void createBooking_whenInvalid_thenReturnError() {
        Long userId = 1L;

        // Устанавливаем некорректную дату окончания
        bookingDto.setEnd(LocalDateTime.now().minusHours(1));

        mockServer.expect(requestTo(serverUrl))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .body("{\"error\":\"Некорректный запрос\", " +
                                "\"message\":\"End date must be after start date\"}")
                        .contentType(MediaType.APPLICATION_JSON));

        ResponseEntity<Object> responseEntity = bookingClient.createBooking(bookingDto, userId);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    @SneakyThrows
    void getBookingById_whenNotFound_thenReturnError() {
        Long bookingId = 999L;
        Long userId = 1L;

        mockServer.expect(requestTo(serverUrl + "/" + bookingId))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .body("{\"error\":\"Бронирование не найдено\", \"message\":\"Нет такого бронирования\"}")
                        .contentType(MediaType.APPLICATION_JSON));

        ResponseEntity<Object> responseEntity = bookingClient.getBookingById(userId, bookingId);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    @SneakyThrows
    void getBookings_whenUnknownState_thenThrowException() {
        Long userId = 1L;
        String unknownState = "UNKNOWN";

        // Теперь мы ожидаем IllegalArgumentException при попытке получить состояние из UNKNOWN
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            BookingState.from(unknownState).orElseThrow(() ->
                    new IllegalArgumentException("Unknown state: " + unknownState));
        });

        assertEquals("Unknown state: " + unknownState, exception.getMessage());
    }
}