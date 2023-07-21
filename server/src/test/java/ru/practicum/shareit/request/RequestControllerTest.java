package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.controller.RequestController;
import ru.practicum.shareit.request.dto.RequestDtoOut;
import ru.practicum.shareit.request.service.RequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.util.UtilConstants.OWNER_ID_HEADER;
import static ru.practicum.shareit.util.UtilConstants.REQUESTS_PATH;

@WebMvcTest(controllers = RequestController.class)
class RequestControllerTest {

    @MockBean
    private RequestService requestService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    private final String requestDescription = "request_test_description";

    private final RequestDtoIn requestDtoIn = RequestDtoIn.builder()
            .description(requestDescription)
            .build();

    private final RequestDtoOut requestDtoOut = RequestDtoOut.builder()
            .id(1L)
            .description(requestDescription)
            .created(LocalDateTime.now())
            .build();

    @SneakyThrows
    @Test
    void post_whenDtoInCorrect_returnDtoOutAndOk() {
        Long authorId = 1L;
        when(requestService.save(requestDtoIn, authorId))
                .thenReturn(requestDtoOut);
        mvc.perform(post(REQUESTS_PATH)
                        .header(OWNER_ID_HEADER, authorId)
                        .content(mapper.writeValueAsString(requestDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDtoOut.getDescription())));
        verify(requestService, times(1))
                .save(any(RequestDtoIn.class), any(Long.class));
    }

    @SneakyThrows
    @Test
    void get_whenCorrect_returnDtoOutAndOk() {
        Long requestId = 1L;
        Long authorId = 1L;
        when(requestService.findById(requestId, authorId))
                .thenReturn(requestDtoOut);
        mvc.perform(get(REQUESTS_PATH + "/{id}", requestId)
                        .header(OWNER_ID_HEADER, authorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDtoOut.getDescription())));
        verify(requestService, times(1))
                .findById(any(Long.class), any(Long.class));
    }

    @SneakyThrows
    @Test
    void getAll_whenCorrect_returnDtoOutAndOk() {
        long userId = 1;
        int from = 0;
        int limit = 1;
        when(requestService.findAll(from, limit, userId))
                .thenReturn(List.of(requestDtoOut));
        mvc.perform(get(REQUESTS_PATH + "/all")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(limit))
                        .header(OWNER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(requestDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(requestDtoOut.getDescription())));
        verify(requestService, times(1))
                .findAll(any(Integer.class), any(Integer.class), any(Long.class));
    }

    @SneakyThrows
    @Test
    void getAll_whenFromAndLimitNull_returnWithDefaultFromAndLimitDtoOutAndOk() {
        when(requestService.findAll(anyInt(), anyInt(), anyLong()))
                .thenReturn(List.of(requestDtoOut));
        mvc.perform(get(REQUESTS_PATH + "/all")
                        .header(OWNER_ID_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
        verify(requestService, never())
                .findAll(any(Integer.class), any(Integer.class), any(Long.class));
    }

    @SneakyThrows
    @Test
    void getAll_whenCreatedByAuthor_thenReturnDtoOut() {
        Long requestId = 1L;
        Long authorId = 1L;
        when(requestService.findAllByAuthorId(authorId))
                .thenReturn(List.of(requestDtoOut));
        mvc.perform(get(REQUESTS_PATH, requestId)
                        .header(OWNER_ID_HEADER, authorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(requestDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(requestDtoOut.getDescription())));
        verify(requestService, times(1))
                .findAllByAuthorId(any(Long.class));
    }
}
