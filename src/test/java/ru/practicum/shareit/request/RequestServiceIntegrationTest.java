package ru.practicum.shareit.request;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.RequestDtoIn;
import ru.practicum.shareit.request.dto.RequestDtoOut;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class RequestServiceIntegrationTest {

    @Autowired
    private RequestServiceImpl requestService;
    @Autowired
    private UserRepository userRepository;

    private final EasyRandom generator = new EasyRandom();

    @Test
    void create() {
        User author = userRepository.save(generator.nextObject(User.class));
        RequestDtoIn dtoIn = generator.nextObject(RequestDtoIn.class);
        RequestDtoOut dtoOut = requestService.create(dtoIn, author.getId());
        assertNotNull(dtoOut.getId());
        assertEquals(dtoIn.getDescription(), dtoOut.getDescription());
        assertNotNull(dtoOut.getCreated());
        assertNull(dtoOut.getItems());
    }

    @Test
    void findAllByAuthorId() {
        User author = userRepository.save(generator.nextObject(User.class));
        RequestDtoIn dtoIn = generator.nextObject(RequestDtoIn.class);
        RequestDtoOut dtoOut = requestService.create(dtoIn, author.getId());
        List<RequestDtoOut> dtoOutList = requestService.findAllByAuthorId(author.getId());
        assertThat(dtoOutList).hasSize(1);
        assertEquals(dtoOut, dtoOutList.get(0));
    }

    @Test
    void findAll() {
        int from = 0;
        int size = 10;
        User author = userRepository.save(generator.nextObject(User.class));
        User anotherUser = userRepository.save(generator.nextObject(User.class));
        RequestDtoIn dtoIn = generator.nextObject(RequestDtoIn.class);
        RequestDtoOut dtoOut = requestService.create(dtoIn, author.getId());
        List<RequestDtoOut> dtoOutList = requestService.findAll(from, size, anotherUser.getId());
        assertThat(dtoOutList).hasSize(1);
        assertEquals(dtoOut, dtoOutList.get(0));
    }
}
