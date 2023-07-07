package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Map;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/users")
@Slf4j
public class UserControllerImpl implements UserController {

    private final UserService service;

    public List<UserDto> getAll() {
        log.debug("Получен запрос на получение списка всех пользователей");
        return service.findAll();
    }

    public UserDto getById(Long id) {
        log.debug("Получен запрос на получение данных о пользователе: {}", id);
        return service.findById(id);
    }

    public UserDto post(UserDto userDto) {
        log.debug("Получен запрос на регистрацию пользователя: {}", userDto);
        if (userDto.getEmail() == null) {
            log.warn("Error! Email cannot be null. {}", userDto);
            throw new IllegalArgumentException("Error! Email cannot be null");
        }
        return service.create(userDto);
    }

    public UserDto put(UserDto userDto) {
        log.debug("Получен запрос на обновление данных о пользователе: {}", userDto);
        return service.update(userDto);
    }

    public UserDto patch(Long id, Map<String, Object> updatedFields) {
        log.debug("Получен запрос на обновление данных о пользователе ид: {}, {}", id, updatedFields);
        return service.patch(id, updatedFields);
    }

    public void delete(Long id) {
        log.debug("Получен запрос на удаление данных о пользователе: {}", id);
        service.delete(id);
    }
}
