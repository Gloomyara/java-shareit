package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.user.dto.UserDtoShort;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.util.UtilConstants.*;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @MockBean
    private BookingServiceImpl bookingService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    private final LocalDateTime start = LocalDateTime.of(2098, 1, 1, 1, 1, 1);
    private final LocalDateTime end = LocalDateTime.of(2099, 1, 1, 1, 1, 1);
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
    private final String bookingPath = BOOKING_PATH;
    private final String ownerIdHeader = OWNER_ID_HEADER;
    private final String itemName = "test_name";
    private final String itemDescription = "test_description";

    private final BookingDtoIn bookingDtoIn = BookingDtoIn.builder()
            .id(1L)
            .start(start)
            .end(end)
            .itemId(1L)
            .build();

    private final ItemDtoShort itemDtoShort = ItemDtoShort.builder()
            .id(1L)
            .name(itemName)
            .description(itemDescription)
            .available(true)
            .ownerId(1L)
            .build();

    private final BookingDtoOut bookingDtoOut = BookingDtoOut.builder()
            .id(1L)
            .start(start)
            .end(end)
            .item(itemDtoShort)
            .booker(new UserDtoShort(1L))
            .status(Status.WAITING)
            .build();

    @SneakyThrows
    @Test
    void post_whenDtoInCorrect_returnDtoOutAndOk() {
        Long bookerId = 1L;
        when(bookingService.save(bookingDtoIn, bookerId))
                .thenReturn(bookingDtoOut);
        mvc.perform(post(bookingPath)
                        .header(ownerIdHeader, bookerId)
                        .content(mapper.writeValueAsString(bookingDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDtoOut.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(bookingDtoOut.getEnd().format(formatter))))
                .andExpect(jsonPath("$.item.id", is(bookingDtoOut.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoOut.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDtoOut.getStatus().toString())));
        verify(bookingService, times(1))
                .save(any(BookingDtoIn.class), any(Long.class));
    }

    @SneakyThrows
    @Test
    void get_whenBookingIdAndBookerIdCorrect_thenReturnDtoOutAndOk() {
        Long bookingId = 1L;
        Long bookerId = 1L;
        when(bookingService.findById(bookingId, bookerId))
                .thenReturn(bookingDtoOut);
        mvc.perform(get(bookingPath + "/{id}", bookingId)
                        .header(ownerIdHeader, bookerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDtoOut.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(bookingDtoOut.getEnd().format(formatter))))
                .andExpect(jsonPath("$.item.id", is(bookingDtoOut.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoOut.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDtoOut.getStatus().toString())));
        verify(bookingService, times(1)).findById(any(Long.class), any(Long.class));
    }

    @SneakyThrows
    @Test
    void put_whenDtoInCorrect_thenReturnDtoOutAndOk() {
        Long bookerId = 1L;
        when(bookingService.update(bookingDtoIn, bookerId))
                .thenReturn(bookingDtoOut);
        mvc.perform(put(bookingPath)
                        .content(mapper.writeValueAsString(bookingDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(ownerIdHeader, bookerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDtoOut.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(bookingDtoOut.getEnd().format(formatter))))
                .andExpect(jsonPath("$.item.id", is(bookingDtoOut.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoOut.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDtoOut.getStatus().toString())));
        verify(bookingService, times(1)).update(any(BookingDtoIn.class), anyLong());
    }

    @SneakyThrows
    @Test
    void patch_whenCorrectParams_returnDtoOut() {
        Long bookingId = 1L;
        Long bookerId = 1L;
        when(bookingService.patch(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDtoOut);
        mvc.perform(patch(bookingPath + "/{id}", bookingId)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(ownerIdHeader, bookerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDtoOut.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(bookingDtoOut.getEnd().format(formatter))))
                .andExpect(jsonPath("$.item.id", is(bookingDtoOut.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoOut.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDtoOut.getStatus().toString())));
        verify(bookingService, times(1)).patch(anyLong(), anyLong(), anyBoolean());
    }

    @SneakyThrows
    @Test
    void getAllByBookerId_whenFromAndLimitNull_returnWithDefaultFromAndLimitDtoOutAndOk() {
        when(bookingService.findAllByBookerId(anyInt(), anyInt(), anyLong(), eq(State.ALL)))
                .thenReturn(List.of(bookingDtoOut));
        mvc.perform(get(bookingPath)
                        .header(ownerIdHeader, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
        verify(bookingService, times(1))
                .findAllByBookerId(any(Integer.class), any(Integer.class), any(Long.class), eq(State.ALL));
    }

    @SneakyThrows
    @Test
    void getAllByItemOwnerId_whenFromAndLimitNull_returnWithDefaultFromAndLimitDtoOutAndOk() {
        when(bookingService.findAllByItemOwnerId(anyInt(), anyInt(), anyLong(), eq(State.ALL)))
                .thenReturn(List.of(bookingDtoOut));
        mvc.perform(get(bookingPath + "/owner")
                        .header(ownerIdHeader, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
        verify(bookingService, times(1))
                .findAllByItemOwnerId(any(Integer.class), any(Integer.class), any(Long.class), eq(State.ALL));
    }
}
