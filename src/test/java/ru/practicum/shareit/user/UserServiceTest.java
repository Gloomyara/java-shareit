package ru.practicum.shareit.user;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.exceptions.user.EmailAlreadyRegisteredException;
import ru.practicum.shareit.exceptions.user.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDtoIn;
import ru.practicum.shareit.user.dto.UserDtoOut;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.util.config.ObjectMapperConfig;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private UserServiceImpl userService;
    private final EasyRandom generator = new EasyRandom();

    @BeforeEach
    void setUp() {
        userService = Mockito.mock(UserServiceImpl.class, CALLS_REAL_METHODS);
    }

    @Test
    void create() {
        ReflectionTestUtils.setField(userService, "userMapper", new UserMapper());
        UserRepository repository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(userService, "repository", repository);
        UserDtoIn dtoIn = generator.nextObject(UserDtoIn.class);
        when(repository.save(any(User.class)))
                .then(returnsFirstArg());
        UserDtoOut dtoOut = userService.create(dtoIn);
        assertEquals(dtoIn.getId(), dtoOut.getId());
        assertEquals(dtoIn.getName(), dtoOut.getName());
        assertEquals(dtoIn.getEmail(), dtoOut.getEmail());
        verify(repository, times(1))
                .save(any(User.class));
    }

    @Test
    void update_whenUserNotExist_assertThrowsUserNotFoundException() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(userService, "repository", userRepository);
        UserDtoIn dtoIn = generator.nextObject(UserDtoIn.class);
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);
        assertThrows(UserNotFoundException.class,
                () -> userService.update(dtoIn));
        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void update_whenUserExist_returnDtoOut() {
        ReflectionTestUtils.setField(userService, "userMapper", new UserMapper());
        UserRepository repository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(userService, "repository", repository);
        UserDtoIn dtoIn = generator.nextObject(UserDtoIn.class);
        when(repository.existsById(anyLong()))
                .thenReturn(true);
        when(repository.save(any(User.class)))
                .then(returnsFirstArg());
        UserDtoOut dtoOut = userService.update(dtoIn);
        assertEquals(dtoIn.getId(), dtoOut.getId());
        assertEquals(dtoIn.getName(), dtoOut.getName());
        assertEquals(dtoIn.getEmail(), dtoOut.getEmail());
        verify(repository, times(1)).save(any(User.class));
    }

    @Test
    void patch_whenEmailRegistered_assertThrowsEmailAlreadyRegisteredException() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(userService, "repository", userRepository);
        when(userRepository.existsByEmailAndIdNot(anyString(), anyLong()))
                .thenReturn(true);
        assertThrows(EmailAlreadyRegisteredException.class,
                () -> userService.patch(generator.nextLong(), Map.of("email", "test@test.omg")));
        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void patch_whenUserNotExist_assertThrowsUserNotFoundException() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(userService, "repository", userRepository);
        when(userRepository.existsByEmailAndIdNot(anyString(), anyLong()))
                .thenReturn(false);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class,
                () -> userService.patch(generator.nextLong(), Map.of("email", "test@test.omg")));
        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void patch_whenCorrect_thanReturnDtoOut() {
        ReflectionTestUtils.setField(userService, "userMapper", new UserMapper());
        UserRepository repository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(userService, "repository", repository);
        ReflectionTestUtils.setField(userService, "objectMapper", new ObjectMapperConfig().objectMapper());
        User user = generator.nextObject(User.class);
        String newEmail = "newtest@test.omg";
        when(repository.existsByEmailAndIdNot(anyString(), anyLong()))
                .thenReturn(false);
        when(repository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(repository.save(any(User.class)))
                .then(returnsFirstArg());
        UserDtoOut dtoOut = userService.patch(generator.nextLong(), Map.of("email", newEmail));
        assertEquals(user.getId(), dtoOut.getId());
        assertEquals(user.getName(), dtoOut.getName());
        assertEquals(newEmail, dtoOut.getEmail());
        verify(repository, times(1))
                .save(any(User.class));
    }

    @Test
    void findById_whenUserNotExist_assertThrowsUserNotFoundException() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(userService, "repository", userRepository);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class,
                () -> userService.findById(generator.nextLong()));
        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void findById_whenCorrect_returnDtoOut() {
        ReflectionTestUtils.setField(userService, "userMapper", new UserMapper());
        UserRepository repository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(userService, "repository", repository);
        User user = generator.nextObject(User.class);
        when(repository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        UserDtoOut dtoOut = userService.findById(generator.nextLong());
        assertEquals(user.getId(), dtoOut.getId());
        assertEquals(user.getName(), dtoOut.getName());
        assertEquals(user.getEmail(), dtoOut.getEmail());
        verify(repository, times(1))
                .findById(anyLong());
    }

    @Test
    void findAll() {
        ReflectionTestUtils.setField(userService, "userMapper", new UserMapper());
        UserRepository repository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(userService, "repository", repository);
        User user = generator.nextObject(User.class);
        when(repository.findAll())
                .thenReturn(List.of(user));
        List<UserDtoOut> foundList = userService.findAll();
        assertThat(foundList).hasSize(1);
        UserDtoOut dtoOut = foundList.get(0);
        assertEquals(user.getId(), dtoOut.getId());
        assertEquals(user.getName(), dtoOut.getName());
        assertEquals(user.getEmail(), dtoOut.getEmail());
        verify(repository, times(1))
                .findAll();
    }

    @Test
    void delete_whenUserNotExist_assertThrowsUserNotFoundException() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(userService, "repository", userRepository);
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);
        assertThrows(UserNotFoundException.class,
                () -> userService.delete(generator.nextLong()));
        verify(userRepository, never())
                .deleteById(anyLong());
    }

    @Test
    void delete() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(userService, "repository", userRepository);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        userService.delete(generator.nextLong());
        verify(userRepository, times(1)).deleteById(anyLong());
    }
}
