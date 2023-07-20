package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.booking.BookingDtoIn;
import ru.practicum.shareit.booking.client.BookingClient;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.util.UtilConstants.*;

@WebMvcTest(controllers = BookingGatewayController.class)
class BookingGatewayControllerTest {

    @MockBean
    private BookingClient bookingClient;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    private final LocalDateTime start = LocalDateTime.of(2098, 1, 1, 1, 1, 1);
    private final LocalDateTime end = LocalDateTime.of(2099, 1, 1, 1, 1, 1);
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
    private final String bookingPath = BOOKING_PATH;
    private final String ownerIdHeader = OWNER_ID_HEADER;

    private final BookingDtoIn bookingDtoIn = BookingDtoIn.builder()
            .id(1L)
            .start(start)
            .end(end)
            .itemId(1L)
            .build();

    @SneakyThrows
    @Test
    void post_whenStartNull_returnBadRequest() {
        bookingDtoIn.setStart(null);
        Long bookerId = 1L;
        when(bookingClient.post(bookingDtoIn, bookerId))
                .thenReturn(null);
        mvc.perform(post(bookingPath)
                        .header(ownerIdHeader, bookerId)
                        .content(mapper.writeValueAsString(bookingDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException));
        verify(bookingClient, never())
                .post(any(BookingDtoIn.class), any(Long.class));
    }

    @SneakyThrows
    @Test
    void post_whenEndNull_returnBadRequest() {
        bookingDtoIn.setEnd(null);
        Long bookerId = 1L;
        when(bookingClient.post(bookingDtoIn, bookerId))
                .thenReturn(null);
        mvc.perform(post(bookingPath)
                        .header(ownerIdHeader, bookerId)
                        .content(mapper.writeValueAsString(bookingDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException));
        verify(bookingClient, never())
                .post(any(BookingDtoIn.class), any(Long.class));
    }

    @SneakyThrows
    @Test
    void post_whenStartPast_returnBadRequest() {
        bookingDtoIn.setStart(LocalDateTime.of(2007, 9, 1, 12, 55, 0));
        Long bookerId = 1L;
        when(bookingClient.post(bookingDtoIn, bookerId))
                .thenReturn(null);
        mvc.perform(post(bookingPath)
                        .header(ownerIdHeader, bookerId)
                        .content(mapper.writeValueAsString(bookingDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException));
        verify(bookingClient, never())
                .post(any(BookingDtoIn.class), any(Long.class));
    }
}
