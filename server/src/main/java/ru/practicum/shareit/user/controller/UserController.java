package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.UserDtoIn;
import ru.practicum.shareit.user.dto.UserDtoOut;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Map;

import static ru.practicum.shareit.util.UtilConstants.USERS_PATH;


@RestController
@RequiredArgsConstructor
@RequestMapping(USERS_PATH)
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDtoOut> getAll() {
        log.info("Received GET {} request.", USERS_PATH);
        return userService.findAll();
    }

    @GetMapping("{id}")
    public UserDtoOut getById(@PathVariable Long id) {
        log.info("Received GET {}/{} request.", USERS_PATH, id);
        return userService.findById(id);
    }

    @PostMapping
    public UserDtoOut post(@RequestBody UserDtoIn dtoIn) {
        log.info("Received POST {} request, userDtoIn = {}", USERS_PATH, dtoIn);
        return userService.save(dtoIn);
    }

    @PutMapping
    public UserDtoOut put(@RequestBody UserDtoIn dtoIn) {
        log.info("Received PUT {} request, userDtoIn = {}", USERS_PATH, dtoIn);
        return userService.update(dtoIn);
    }

    @PatchMapping("{id}")
    public UserDtoOut patch(@PathVariable Long id,
                            @RequestBody Map<String, Object> fields) {
        log.info("Received PATCH {}/{} request, fields = {}", USERS_PATH, id, fields);
        return userService.patch(id, fields);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("Received DELETE {}/{} request.", USERS_PATH, id);
        userService.delete(id);
    }
}
