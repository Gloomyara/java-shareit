package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDtoIn;
import ru.practicum.shareit.user.dto.UserDtoOut;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Map;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService service;

    @GetMapping
    public List<UserDtoOut> getAll() {
        log.info("Получен запрос на получение списка всех пользователей");
        return service.findAll();
    }

    @GetMapping("{id}")
    public UserDtoOut getById(@PathVariable @Positive Long id) {
        log.info("Получен запрос на получение данных о пользователе: {}", id);
        return service.findById(id);
    }

    @PostMapping
    UserDtoOut post(@Valid @RequestBody UserDtoIn dtoIn) {
        log.info("Получен запрос на регистрацию пользователя: {}", dtoIn);
        if (dtoIn.getEmail() == null) {
            log.warn("Error! Email cannot be null. {}", dtoIn);
            throw new IllegalArgumentException("Error! Email cannot be null");
        }
        return service.create(dtoIn);
    }

    @PutMapping
    UserDtoOut put(@Valid @RequestBody UserDtoIn dtoIn) {
        log.info("Получен запрос на обновление данных о пользователе: {}", dtoIn);
        return service.update(dtoIn);
    }

    @PatchMapping("{id}")
    UserDtoOut patch(@PathVariable @Positive Long id,
                     @RequestBody Map<String, Object> fields) {
        log.info("Получен запрос на обновление данных о пользователе ид: {}, {}", id, fields);
        return service.patch(id, fields);
    }

    @DeleteMapping("/{id}")
    void delete(@PathVariable @Positive Long id) {
        log.info("Получен запрос на удаление данных о пользователе: {}", id);
        service.delete(id);
    }
}
