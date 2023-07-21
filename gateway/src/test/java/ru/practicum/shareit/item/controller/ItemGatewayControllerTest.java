package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.item.CommentDtoIn;
import ru.practicum.shareit.item.ItemDtoIn;
import ru.practicum.shareit.item.client.ItemClient;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.util.UtilConstants.ITEM_PATH;
import static ru.practicum.shareit.util.UtilConstants.OWNER_ID_HEADER;

@WebMvcTest(controllers = ItemGatewayController.class)
class ItemGatewayControllerTest {

    @MockBean
    private ItemClient itemClient;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    private final String itemPath = ITEM_PATH;
    private final String ownerIdHeader = OWNER_ID_HEADER;

    private final ItemDtoIn itemDtoIn = ItemDtoIn.builder()
            .id(1L)
            .name("test_name")
            .description("test_description")
            .available(true)
            .build();

    private final CommentDtoIn commentDtoIn = CommentDtoIn.builder()
            .text("test_text")
            .build();

    @SneakyThrows
    @Test
    void post_whenNameIsBlank_returnBadRequest() {
        itemDtoIn.setName(" ");
        Long ownerId = 1L;
        when(itemClient.post(itemDtoIn, ownerId))
                .thenReturn(null);
        mvc.perform(post(itemPath)
                        .header(ownerIdHeader, ownerId)
                        .content(mapper.writeValueAsString(itemDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException));
        verify(itemClient, never())
                .post(any(ItemDtoIn.class), any(Long.class));
    }

    @SneakyThrows
    @Test
    void post_whenDescriptionIsBlank_returnBadRequest() {
        itemDtoIn.setDescription(" ");
        Long ownerId = 1L;
        when(itemClient.post(itemDtoIn, ownerId))
                .thenReturn(null);
        mvc.perform(post(itemPath)
                        .header(ownerIdHeader, ownerId)
                        .content(mapper.writeValueAsString(itemDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException));
        verify(itemClient, never())
                .post(any(ItemDtoIn.class), any(Long.class));
    }

    @SneakyThrows
    @Test
    void post_whenAvailableNull_returnBadRequest() {
        itemDtoIn.setAvailable(null);
        Long ownerId = 1L;
        when(itemClient.post(itemDtoIn, ownerId))
                .thenReturn(null);
        mvc.perform(post(itemPath)
                        .header(ownerIdHeader, ownerId)
                        .content(mapper.writeValueAsString(itemDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException));
        verify(itemClient, never())
                .post(any(ItemDtoIn.class), any(Long.class));
    }
}
