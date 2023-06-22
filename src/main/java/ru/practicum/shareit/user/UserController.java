package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService service;

    @GetMapping
    public Collection<UserDto> getAllUsers() {
        log.debug("Получен запрос на получение списка всех пользователей");
        return service.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable Long userId) {
        log.debug("Получен запрос на получение данных о пользователе: {}", userId);
        return service.getById(userId);
    }

    @PostMapping
    public UserDto saveNewUser(@RequestBody @Valid UserDto userDto) {
        log.debug("Получен запрос на регистрацию пользователя: {}", userDto);
        if (userDto.getEmail() == null) {
            log.warn("Error! Email cannot be null. {}", userDto);
            throw new IllegalArgumentException("Error! Email cannot be null");
        }
        return service.saveUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto patchUser(@PathVariable Long userId,
                             @RequestBody @Valid UserDto userDto) {
        log.debug("Получен запрос на обновление данных о пользователе: {}", userDto);
        return service.patch(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.debug("Получен запрос на удаление данных о пользователе: {}", userId);
        service.delete(userId);
    }
}