package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.item.comment.dto.CommentDtoIn;
import ru.practicum.shareit.item.comment.dto.CommentDtoOut;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.util.UtilConstants.ITEM_PATH;
import static ru.practicum.shareit.util.UtilConstants.OWNER_ID_HEADER;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @MockBean
    private ItemServiceImpl itemService;
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

    private final ItemDtoOut itemDtoOut = ItemDtoOut.builder()
            .id(1L)
            .name("test_name")
            .description("test_description")
            .available(true)
            .build();

    private final CommentDtoIn commentDtoIn = CommentDtoIn.builder()
            .text("test_text")
            .build();

    private final CommentDtoOut commentDtoOut = CommentDtoOut.builder()
            .id(1L)
            .text("test_text")
            .authorName("test_user")
            .created(LocalDateTime.now())
            .build();

    @SneakyThrows
    @Test
    void post_whenDtoInCorrect_returnDtoOutAndOk() {
        Long ownerId = 1L;
        when(itemService.create(itemDtoIn, ownerId))
                .thenReturn(itemDtoOut);
        mvc.perform(post(itemPath)
                        .header(ownerIdHeader, ownerId)
                        .content(mapper.writeValueAsString(itemDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoOut.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoOut.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoOut.getAvailable())));
        verify(itemService, times(1))
                .create(any(ItemDtoIn.class), any(Long.class));
    }

    @SneakyThrows
    @Test
    void post_whenNameIsBlank_returnBadRequest() {
        itemDtoIn.setName(" ");
        Long ownerId = 1L;
        when(itemService.create(itemDtoIn, ownerId))
                .thenReturn(itemDtoOut);
        mvc.perform(post(itemPath)
                        .header(ownerIdHeader, ownerId)
                        .content(mapper.writeValueAsString(itemDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException));
        verify(itemService, never())
                .create(any(ItemDtoIn.class), any(Long.class));
    }

    @SneakyThrows
    @Test
    void post_whenDescriptionIsBlank_returnBadRequest() {
        itemDtoIn.setDescription(" ");
        Long ownerId = 1L;
        when(itemService.create(itemDtoIn, ownerId))
                .thenReturn(itemDtoOut);
        mvc.perform(post(itemPath)
                        .header(ownerIdHeader, ownerId)
                        .content(mapper.writeValueAsString(itemDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException));
        verify(itemService, never())
                .create(any(ItemDtoIn.class), any(Long.class));
    }

    @SneakyThrows
    @Test
    void post_whenAvailableNull_returnBadRequest() {
        itemDtoIn.setAvailable(null);
        Long ownerId = 1L;
        when(itemService.create(itemDtoIn, ownerId))
                .thenReturn(itemDtoOut);
        mvc.perform(post(itemPath)
                        .header(ownerIdHeader, ownerId)
                        .content(mapper.writeValueAsString(itemDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException));
        verify(itemService, never())
                .create(any(ItemDtoIn.class), any(Long.class));
    }

    @SneakyThrows
    @Test
    void get_whenItemIdAndOwnerIdCorrect_returnDtoOut() {
        Long itemId = 1L;
        Long ownerId = 1L;
        when(itemService.findById(itemId, ownerId))
                .thenReturn(itemDtoOut);
        mvc.perform(get(itemPath + "/{id}", itemId)
                        .header(ownerIdHeader, ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoOut.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoOut.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoOut.getAvailable())));
        verify(itemService, times(1)).findById(any(Long.class), any(Long.class));
    }

    @SneakyThrows
    @Test
    void put_whenDtoInCorrect_returnDtoOut() {
        Long ownerId = 1L;
        when(itemService.update(itemDtoIn, ownerId))
                .thenReturn(itemDtoOut);
        mvc.perform(put(itemPath)
                        .content(mapper.writeValueAsString(itemDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(ownerIdHeader, ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoOut.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoOut.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoOut.getAvailable())));
        verify(itemService, times(1)).update(any(ItemDtoIn.class), anyLong());
    }

    @SneakyThrows
    @Test
    void patch_() {
        Long itemId = 1L;
        Long ownerId = 1L;
        when(itemService.patch(itemId, Map.of(), ownerId))
                .thenReturn(itemDtoOut);
        mvc.perform(patch(itemPath + "/{id}", itemId)
                        .content(mapper.writeValueAsString(Map.of()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(ownerIdHeader, ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoOut.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoOut.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoOut.getAvailable())));
        verify(itemService, times(1)).patch(anyLong(), anyMap(), anyLong());
    }

    @SneakyThrows
    @Test
    void getAllByOwnerId_whenFromAndLimitNull_returnWithDefaultFromAndLimitDtoOutAndOk() {
        when(itemService.findAllByOwnerId(anyInt(), anyInt(), anyLong()))
                .thenReturn(List.of(itemDtoOut));
        mvc.perform(get(itemPath)
                        .header(ownerIdHeader, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
        verify(itemService, times(1))
                .findAllByOwnerId(any(Integer.class), any(Integer.class), any(Long.class));
    }

    @SneakyThrows
    @Test
    void searchByNameOrDescription_whenCorrect_returnDtoOutAndOk() {
        when(itemService.searchByNameOrDescription(anyInt(), anyInt(), anyString()))
                .thenReturn(List.of(itemDtoOut));
        mvc.perform(get(itemPath + "/search")
                        .param("text", "name")
                        .header(ownerIdHeader, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDtoOut.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDtoOut.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDtoOut.getAvailable())));
        verify(itemService, times(1))
                .searchByNameOrDescription(anyInt(), anyInt(), anyString());
    }

    @SneakyThrows
    @Test
    void postComment_whenCorrect_returnDtoOutAndOk() {
        Long itemId = 1L;
        when(itemService.createComment(anyLong(), anyLong(), eq(commentDtoIn)))
                .thenReturn(commentDtoOut);
        mvc.perform(post(itemPath + "/{id}/comment", itemId)
                        .header(ownerIdHeader, 1)
                        .content(mapper.writeValueAsString(commentDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDtoOut.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDtoOut.getAuthorName())))
                .andExpect(jsonPath("$.created").exists());
        verify(itemService, times(1))
                .createComment(anyLong(), anyLong(), any(CommentDtoIn.class));
    }
}
