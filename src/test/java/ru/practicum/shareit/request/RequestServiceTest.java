package ru.practicum.shareit.request;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.user.UserNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.RequestDtoIn;
import ru.practicum.shareit.request.dto.RequestDtoOut;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.pager.PageRequester;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.util.UtilConstants.DEFAULT_FROM;
import static ru.practicum.shareit.util.UtilConstants.DEFAULT_LIMIT;

@ExtendWith(MockitoExtension.class)
class RequestServiceTest {

    private RequestService requestService;
    private final EasyRandom generator = new EasyRandom();

    private final int from = Integer.parseInt(DEFAULT_FROM);
    private final int limit = Integer.parseInt(DEFAULT_LIMIT);

    @BeforeEach
    void setUp() {
        requestService = Mockito.mock(RequestServiceImpl.class, CALLS_REAL_METHODS);
    }

    @Test
    void create_whenAuthorNotExists_assertThrowsUserNotFoundException() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(requestService, "userRepository", userRepository);
        RequestRepository requestRepository = Mockito.mock(RequestRepository.class);
        ReflectionTestUtils.setField(requestService, "repository", requestRepository);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class,
                () -> requestService.create(generator.nextObject(RequestDtoIn.class), generator.nextLong()));
        verify(requestRepository, never()).save(any(Request.class));
    }

    @Test
    void create_whenAuthorExists_returnDtoOut() {
        ReflectionTestUtils.setField(requestService, "requestMapper", new RequestMapper());
        ReflectionTestUtils.setField(requestService, "itemMapper", new ItemMapper());
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(requestService, "userRepository", userRepository);
        RequestRepository requestRepository = Mockito.mock(RequestRepository.class);
        ReflectionTestUtils.setField(requestService, "repository", requestRepository);
        User user = generator.nextObject(User.class);
        RequestDtoIn dtoIn = generator.nextObject(RequestDtoIn.class);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(requestRepository.save(any(Request.class)))
                .then(returnsFirstArg());
        RequestDtoOut dtoOut = requestService.create(dtoIn, user.getId());
        assertNotNull(dtoOut.getId());
        assertEquals(dtoIn.getDescription(), dtoOut.getDescription());
        assertNotNull(dtoOut.getCreated());
        assertNull(dtoOut.getItems());
        verify(requestRepository, times(1))
                .save(any(Request.class));
    }

    @Test
    void findById_whenAuthorNotExists_assertThrowsUserNotFoundException() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(requestService, "userRepository", userRepository);
        RequestRepository requestRepository = Mockito.mock(RequestRepository.class);
        ReflectionTestUtils.setField(requestService, "repository", requestRepository);
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);
        assertThrows(UserNotFoundException.class,
                () -> requestService.findById(generator.nextLong(), generator.nextLong()));
        verify(requestRepository, never()).save(any(Request.class));
    }

    @Test
    void findById_whenIdNotExists_assertThrowsEntityNotFoundException() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(requestService, "userRepository", userRepository);
        RequestRepository requestRepository = Mockito.mock(RequestRepository.class);
        ReflectionTestUtils.setField(requestService, "repository", requestRepository);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> requestService.findById(generator.nextLong(), generator.nextLong()));
        verify(requestRepository, never()).save(any(Request.class));
    }

    @Test
    void findById_whenCorrect_returnDtoOut() {
        ReflectionTestUtils.setField(requestService, "requestMapper", new RequestMapper());
        ReflectionTestUtils.setField(requestService, "itemMapper", new ItemMapper());
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(requestService, "userRepository", userRepository);
        RequestRepository requestRepository = Mockito.mock(RequestRepository.class);
        ReflectionTestUtils.setField(requestService, "repository", requestRepository);
        Request request = generator.nextObject(Request.class);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.of(request));
        RequestDtoOut dtoOut = requestService.findById(generator.nextLong(), generator.nextLong());
        assertEquals(request.getId(), dtoOut.getId());
        assertEquals(request.getDescription(), dtoOut.getDescription());
        assertEquals(request.getCreated(), dtoOut.getCreated());
        assertNotNull(dtoOut.getItems());
        verify(requestRepository, times(1)).findById(anyLong());
    }

    @Test
    void findAll_whenUserNotFound_assertThrowsUserNotFoundException() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(requestService, "userRepository", userRepository);
        RequestRepository requestRepository = Mockito.mock(RequestRepository.class);
        ReflectionTestUtils.setField(requestService, "repository", requestRepository);
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);
        assertThrows(UserNotFoundException.class,
                () -> requestService.findAll(from, limit, generator.nextLong()));
        verify(requestRepository, never()).findAllOtherRequests(anyLong(), any(PageRequester.class));
    }

    @Test
    void findAll_whenUserExist_returnDtoOut() {
        ReflectionTestUtils.setField(requestService, "requestMapper", new RequestMapper());
        ReflectionTestUtils.setField(requestService, "itemMapper", new ItemMapper());
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(requestService, "userRepository", userRepository);
        RequestRepository requestRepository = Mockito.mock(RequestRepository.class);
        ReflectionTestUtils.setField(requestService, "repository", requestRepository);
        Request request = generator.nextObject(Request.class);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(requestRepository.findAllOtherRequests(anyLong(), any(PageRequester.class)))
                .thenReturn(new PageImpl<>(List.of(request)));
        List<RequestDtoOut> foundList = requestService.findAll(from, limit, generator.nextLong());
        assertThat(foundList).hasSize(1);
        RequestDtoOut dtoOut = foundList.get(0);
        assertEquals(request.getId(), dtoOut.getId());
        assertEquals(request.getDescription(), dtoOut.getDescription());
        assertEquals(request.getCreated(), dtoOut.getCreated());
        assertNotNull(dtoOut.getItems());
        verify(requestRepository, times(1))
                .findAllOtherRequests(anyLong(), any(Pageable.class));
    }

    @Test
    void findAllByAuthorId_whenUserNotFound_assertThrowsUserNotFoundException() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(requestService, "userRepository", userRepository);
        RequestRepository requestRepository = Mockito.mock(RequestRepository.class);
        ReflectionTestUtils.setField(requestService, "repository", requestRepository);
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);
        assertThrows(UserNotFoundException.class,
                () -> requestService.findAllByAuthorId(generator.nextLong()));
        verify(requestRepository, never()).findAllByAuthorIdWithItems(anyLong());
    }

    @Test
    void findAllByAuthorId_whenAuthor_returnDroOut() {
        ReflectionTestUtils.setField(requestService, "requestMapper", new RequestMapper());
        ReflectionTestUtils.setField(requestService, "itemMapper", new ItemMapper());
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(requestService, "userRepository", userRepository);
        RequestRepository requestRepository = Mockito.mock(RequestRepository.class);
        ReflectionTestUtils.setField(requestService, "repository", requestRepository);
        Request request = generator.nextObject(Request.class);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(requestRepository.findAllByAuthorIdWithItems(anyLong()))
                .thenReturn(List.of(request));
        List<RequestDtoOut> foundList = requestService.findAllByAuthorId(generator.nextLong());
        assertThat(foundList).hasSize(1);
        RequestDtoOut dtoOut = foundList.get(0);
        assertEquals(request.getId(), dtoOut.getId());
        assertEquals(request.getDescription(), dtoOut.getDescription());
        assertEquals(request.getCreated(), dtoOut.getCreated());
        assertNotNull(dtoOut.getItems());
        verify(requestRepository, times(1))
                .findAllByAuthorIdWithItems(anyLong());
    }
}
