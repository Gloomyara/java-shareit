package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.user.UserDtoIn;
import ru.practicum.shareit.user.client.UserClient;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.util.UtilConstants.USERS_PATH;

@WebMvcTest(controllers = UserGatewayController.class)
class UserGatewayControllerTest {

    @MockBean
    private UserClient userClient;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    private final String userPath = USERS_PATH;

    private final UserDtoIn userDtoIn = UserDtoIn.builder()
            .id(1L)
            .name("test_user")
            .email("test@test.omg")
            .build();

    @SneakyThrows
    @Test
    void post_whenEmailIncorrect_returnBadRequest() {
        userDtoIn.setEmail("email.ru");
        when(userClient.post(userDtoIn))
                .thenReturn(null);
        mvc.perform(MockMvcRequestBuilders.post(userPath)
                        .content(mapper.writeValueAsString(userDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
        verify(userClient, never()).post(any(UserDtoIn.class));
    }

    @SneakyThrows
    @Test
    void post_whenNameIsBlank_returnBadRequest() {
        userDtoIn.setName(" ");
        when(userClient.post(userDtoIn))
                .thenReturn(null);
        mvc.perform(MockMvcRequestBuilders.post(userPath)
                        .content(mapper.writeValueAsString(userDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andDo(print());
        verify(userClient, never()).post(any(UserDtoIn.class));
    }
}
