package ru.practicum.shareit.booking;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.client.BaseClient;


@Slf4j
@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> getBookings(long userId, BookingState state, Integer from, Integer size) {
        if (state == null) {
            throw new IllegalArgumentException("Unknown state: " + state);
        }
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }


/*    public ResponseEntity<Object> getBookings(long userId, BookingState state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }*/

    public ResponseEntity<Object> createBooking(BookingDto bookingDto, Long userId) {
        return post("", userId, bookingDto);
    }

    public ResponseEntity<Object> updateBooking(Long bookingId, Long userId, Boolean approved) {
        Map<String, Object> param = Map.of("approved", approved);
        return patch("/" + bookingId + "?approved={approved}", userId, param, null);
    }

    public ResponseEntity<Object> getBookingById(long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getAllBookingsByOwner(Long bookerId, BookingState state) {
        return get("/owner?state=" + state.name(), bookerId);
    }

    public ResponseEntity<Object> approveOrRejectBooking(Long userId, Long bookingId, Boolean approved) {
        return patch("/" + bookingId + "?approved=" + approved, userId);
    }
}
