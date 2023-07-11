package ru.practicum.shareit.user.mapper;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.abstraction.mapper.ModelMapper;
import ru.practicum.shareit.abstraction.model.DtoIn;
import ru.practicum.shareit.user.dto.UserDtoIn;
import ru.practicum.shareit.user.dto.UserDtoOut;
import ru.practicum.shareit.user.dto.UserDtoShort;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserMapper implements ModelMapper<User> {

    @Override
    public UserDtoOut toDto(User user) {

        return UserDtoOut.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    @Override
    public User dtoToEntity(DtoIn in) {
        UserDtoIn userDtoIn = (UserDtoIn) in;
        return User.builder()
                .id(userDtoIn.getId())
                .name(userDtoIn.getName())
                .email(userDtoIn.getEmail())
                .build();
    }

    @Override
    public List<UserDtoOut> toDto(List<User> entities) {
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }

    public UserDtoShort toDtoShort(User user) {
        return UserDtoShort.builder()
                .id(user.getId())
                .build();
    }
}
