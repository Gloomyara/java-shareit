package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService service;

    @GetMapping
    public Collection<User> getAllUsers() {
        return service.getAllUsers();
    }

    @GetMapping("/{userId}")
    public User getById(@PathVariable Long userId) {
        return service.getById(userId);
    }

    @PostMapping
    public User saveNewUser(@RequestBody @Valid UserDto userDto) {
        if (userDto.getEmail() == null) {
            throw new IllegalArgumentException("Error! Email cannot be null");
        }
        return service.saveUser(userDto);
    }

    @PatchMapping("/{userId}")
    public User patchUser(@PathVariable Long userId,
                          @RequestBody @Valid UserDto userDto) {
        return service.patch(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        service.delete(userId);
    }
}