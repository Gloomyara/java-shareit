package ru.practicum.shareit.booking.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.abstraction.GatewayClient;
import ru.practicum.shareit.booking.BookingDtoIn;
import ru.practicum.shareit.booking.State;

import java.util.Map;

import static ru.practicum.shareit.util.UtilConstants.BOOKING_PATH;
import static ru.practicum.shareit.util.UtilConstants.BOOKING_STATE;

@Service
public class BookingClient extends GatewayClient {

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + BOOKING_PATH))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getAllByUserId(Integer from, Integer size, Long userId, State state) {
        Map<String, Object> params = Map.of(
                BOOKING_STATE, state.name(),
                "from", from,
                "size", size
        );
        return get("?" + BOOKING_STATE + "={" + BOOKING_STATE + "}&from={from}&size={size}",
                userId, params);
    }

    public ResponseEntity<Object> getAllByOwnerId(Integer from, Integer size, Long userId, State state) {
        Map<String, Object> params = Map.of(
                BOOKING_STATE, state.name(),
                "from", from,
                "size", size
        );
        return get("/owner?" + BOOKING_STATE + "={" + BOOKING_STATE + "}&from={from}&size={size}",
                userId, params);
    }

    public ResponseEntity<Object> getById(Long bookingId, Long userId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> post(BookingDtoIn bookingDtoIn, Long userId) {
        return post("", userId, bookingDtoIn);
    }

    public ResponseEntity<Object> put(BookingDtoIn bookingDtoIn, Long userId) {
        return put("", userId, bookingDtoIn);
    }

    public ResponseEntity<Object> patch(Long bookingId, long userId, boolean approved) {
        return patch("/" + bookingId + "?approved=" + approved, userId);
    }
}
