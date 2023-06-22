package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.model.UserMapper.dtoToUser;
import static ru.practicum.shareit.user.model.UserMapper.toUserDto;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public Collection<UserDto> getAllUsers() {
        return repository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto saveUser(UserDto userDto) {
        return toUserDto(repository.create(dtoToUser(userDto)));
    }

    @Override
    public UserDto getById(Long id) {
        return toUserDto(repository.getById(id).orElse(null));
    }

    @Override
    public UserDto patch(Long id, UserDto userDto) {
        repository.containsOrElseThrow(id);
        return toUserDto(repository.patch(id, userDto));
    }

    @Override
    public void delete(Long id) {
        repository.delete(id);
    }
}