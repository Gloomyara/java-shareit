package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.UserDtoIn;
import ru.practicum.shareit.user.client.UserClient;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.Map;

import static ru.practicum.shareit.util.UtilConstants.USERS_PATH;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(USERS_PATH)
public class UserGatewayController {

    private final UserClient client;

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("Received GET {} request.", USERS_PATH);
        return client.getAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<Object> getById(@PathVariable @Positive Long id) {
        log.info("Received GET {}/{} request.", USERS_PATH, id);
        return client.getById(id);
    }

    @PostMapping
    public ResponseEntity<Object> post(@RequestBody @Valid UserDtoIn dtoIn) {
        log.info("Received POST {} request, userDtoIn = {}", USERS_PATH, dtoIn);
        return client.post(dtoIn);
    }

    @PutMapping
    public ResponseEntity<Object> put(@RequestBody @Valid UserDtoIn dtoIn) {
        log.info("Received PUT {} request, userDtoIn = {}", USERS_PATH, dtoIn);
        return client.put(dtoIn);
    }

    @PatchMapping("{id}")
    public ResponseEntity<Object> patch(@PathVariable @Positive Long id,
                                        @RequestBody Map<String, Object> fields) {
        log.info("Received PATCH {}/{} request, fields = {}", USERS_PATH, id, fields);
        return client.patch(id, fields);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable @Positive Long id) {
        log.info("Received DELETE {}/{} request.", USERS_PATH, id);
        client.delete(id);
    }
}
