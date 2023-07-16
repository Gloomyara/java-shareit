package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDtoIn;
import ru.practicum.shareit.user.dto.UserDtoOut;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.util.UtilConstants.USER_PATH;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @MockBean
    private UserService userService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    private final String userPath = USER_PATH;

    private final UserDtoOut userDtoOut = UserDtoOut.builder()
            .id(1L)
            .name("test_user")
            .email("test@test.omg")
            .build();

    @SneakyThrows
    @Test
    void post_whenDtoInCorrect_returnDtoOutAndOk() {
        when(userService.create(any(UserDtoIn.class)))
                .thenReturn(userDtoOut);
        mvc.perform(MockMvcRequestBuilders.post(userPath)
                        .content(mapper.writeValueAsString(userDtoOut))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDtoOut.getName())))
                .andExpect(jsonPath("$.email", is(userDtoOut.getEmail())));
        verify(userService, times(1)).create(any(UserDtoIn.class));
    }

    @SneakyThrows
    @Test
    void post_whenEmailIncorrect_returnBadRequest() {
        userDtoOut.setEmail("email.ru");
        when(userService.create(any(UserDtoIn.class)))
                .thenReturn(userDtoOut);
        mvc.perform(MockMvcRequestBuilders.post(userPath)
                        .content(mapper.writeValueAsString(userDtoOut))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
        verify(userService, never()).create(any(UserDtoIn.class));
    }

    @SneakyThrows
    @Test
    void post_whenNameIsBlank_returnBadRequest() {
        userDtoOut.setName(" ");
        when(userService.create(any(UserDtoIn.class)))
                .thenReturn(userDtoOut);
        mvc.perform(MockMvcRequestBuilders.post(userPath)
                        .content(mapper.writeValueAsString(userDtoOut))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andDo(print());
        verify(userService, never()).create(any(UserDtoIn.class));
    }

    @SneakyThrows
    @Test
    void get_byId() {
        Long id = 1L;
        when(userService.findById(id))
                .thenReturn(userDtoOut);
        mvc.perform(get(userPath + "/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDtoOut.getName())))
                .andExpect(jsonPath("$.email", is(userDtoOut.getEmail())))
                .andDo(print());
        verify(userService, times(1)).findById(anyLong());
    }

    @SneakyThrows
    @Test
    void getAll() {
        when(userService.findAll())
                .thenReturn(List.of(userDtoOut));
        mvc.perform(get(userPath))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(userDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userDtoOut.getName())))
                .andExpect(jsonPath("$[0].email", is(userDtoOut.getEmail())))
                .andDo(print());
        verify(userService, times(1)).findAll();
    }

    @SneakyThrows
    @Test
    void putUser() {
        UserDtoIn updUserDtoIn = UserDtoIn.builder()
                .id(1L)
                .name("upd_user")
                .email("upd_test@test.omg")
                .build();
        UserDtoOut updUserDtoOut = UserDtoOut.builder()
                .id(1L)
                .name("upd_user")
                .email("upd_test@test.omg")
                .build();
        when(userService.update(updUserDtoIn))
                .thenReturn(updUserDtoOut);
        mvc.perform(put(userPath)
                        .content(mapper.writeValueAsString(updUserDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updUserDtoIn.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(updUserDtoIn.getName())))
                .andExpect(jsonPath("$.email", is(updUserDtoIn.getEmail())));
        verify(userService, times(1)).update(any(UserDtoIn.class));
    }

    @SneakyThrows
    @Test
    void patchUser() {
        Long userId = 1L;
        when(userService.patch(userId, Map.of()))
                .thenReturn(userDtoOut);
        mvc.perform(patch(userPath + "/{id}", userId)
                        .content(mapper.writeValueAsString(Map.of()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDtoOut.getName())))
                .andExpect(jsonPath("$.email", is(userDtoOut.getEmail())));
        verify(userService, times(1)).patch(anyLong(), anyMap());
    }

    @SneakyThrows
    @Test
    void deleteUser() {
        Long userId = 1L;
        mvc.perform(delete(userPath + "/{id}", userId)
                        .content(mapper.writeValueAsString(Map.of()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(userService, times(1)).delete(anyLong());
    }
}
