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

import static ru.practicum.shareit.util.UtilConstants.USER_PATH;


@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(USER_PATH)
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDtoOut> getAll() {
        log.info("Received GET {} request.", USER_PATH);
        return userService.findAll();
    }

    @GetMapping("{id}")
    public UserDtoOut getById(@PathVariable @Positive Long id) {
        log.info("Received GET {}/{} request.", USER_PATH, id);
        return userService.findById(id);
    }

    @PostMapping
    public UserDtoOut post(@Valid @RequestBody UserDtoIn dtoIn) {
        log.info("Received POST {} request, userDtoIn = {}", USER_PATH, dtoIn);
        return userService.create(dtoIn);
    }

    @PutMapping
    public UserDtoOut put(@Valid @RequestBody UserDtoIn dtoIn) {
        log.info("Received PUT {} request, userDtoIn = {}", USER_PATH, dtoIn);
        return userService.update(dtoIn);
    }

    @PatchMapping("{id}")
    public UserDtoOut patch(@PathVariable @Positive Long id,
                            @RequestBody Map<String, Object> fields) {
        log.info("Received PATCH {}/{} request, fields = {}", USER_PATH, id, fields);
        return userService.patch(id, fields);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable @Positive Long id) {
        log.info("Received DELETE {}/{} request.", USER_PATH, id);
        userService.delete(id);
    }
}
