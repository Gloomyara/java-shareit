package ru.practicum.shareit.user;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.user.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDtoOut;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserServiceImpl userService;

    private final EasyRandom generator = new EasyRandom();

    @Test
    void create_whenDtoInCorrect_returnDtoOut() {
        UserDtoIn dtoIn = generator.nextObject(UserDtoIn.class);
        dtoIn.setId(null);
        UserDtoOut dtoOut = userService.save(dtoIn);
        dtoIn.setId(dtoOut.getId());
        assertEquals(dtoOut.getId(), dtoIn.getId());
        assertEquals(dtoOut.getName(), dtoIn.getName());
        assertEquals(dtoOut.getEmail(), dtoIn.getEmail());
    }

    @Test
    void update_whenUserExist_returnDtoOut() {
        UserDtoIn dtoIn = generator.nextObject(UserDtoIn.class);
        dtoIn.setId(null);
        UserDtoOut created = userService.save(dtoIn);
        dtoIn.setId(created.getId());
        dtoIn.setEmail(generator.nextObject(String.class));
        dtoIn.setName(generator.nextObject(String.class));
        UserDtoOut dtoOut = userService.update(dtoIn);
        assertEquals(dtoOut.getId(), dtoIn.getId());
        assertEquals(dtoOut.getName(), dtoIn.getName());
        assertEquals(dtoOut.getEmail(), dtoIn.getEmail());
    }

    @Test
    void patch_whenParamsCorrect_returnDtoOut() {
        UserDtoIn dtoIn = generator.nextObject(UserDtoIn.class);
        dtoIn.setId(null);
        UserDtoOut dtoOut = userService.save(dtoIn);
        String correctEmail = "qwerty@email.com";
        dtoOut.setEmail(correctEmail);
        assertEquals(userService.patch(dtoOut.getId(), Map.of("email", correctEmail)), dtoOut);
    }

    @Test
    void findById_whenUserExist_returnDtoOut() {
        UserDtoIn dtoIn = generator.nextObject(UserDtoIn.class);
        dtoIn.setId(null);
        UserDtoOut dtoOut = userService.save(dtoIn);
        dtoIn.setId(dtoOut.getId());
        assertEquals(userService.findById(dtoOut.getId()), dtoOut);
    }

    @Test
    void findAll() {
        List<UserDtoOut> empty = userService.findAll();
        assertThat(empty).isEmpty();
        UserDtoIn dtoIn = generator.nextObject(UserDtoIn.class);
        dtoIn.setId(null);
        UserDtoOut dtoOut = userService.save(dtoIn);
        List<UserDtoOut> all = userService.findAll();
        assertArrayEquals(all.toArray(), List.of(dtoOut).toArray());
    }

    @Test
    void delete() {
        UserDtoIn dtoIn = generator.nextObject(UserDtoIn.class);
        dtoIn.setId(null);
        UserDtoOut dtoOut = userService.save(dtoIn);
        userService.delete(dtoOut.getId());
        assertThrows(UserNotFoundException.class,
                () -> userService.delete(dtoOut.getId()));
    }
}
