package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper mapper;

    @Override
    public Collection<User> getAllUsers() {
        return repository.findAll();
    }

    @Override
    public User saveUser(UserDto userDto) {
        return repository.create(mapper.toObject(userDto));
    }

    @Override
    public User patch(Long id, UserDto userDto) {
        User user = mapper.toObject(userDto);
        user.setId(id);
        return repository.patch(user);
    }

    @Override
    public void delete(Long id) {
        repository.delete(id);
    }
}