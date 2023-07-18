package ru.practicum.shareit.user.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.abstraction.mapper.ModelMapper;
import ru.practicum.shareit.user.dto.UserDtoIn;
import ru.practicum.shareit.user.dto.UserDtoOut;
import ru.practicum.shareit.user.dto.UserDtoShort;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper implements ModelMapper<UserDtoIn, UserDtoOut, User> {

    @Override
    public UserDtoOut toDto(User entity) {
        if (entity == null) {
            return null;
        }
        return UserDtoOut.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .build();
    }

    @Override
    public User toEntity(UserDtoIn dtoIn) {
        if (dtoIn == null) {
            return null;
        }
        return User.builder()
                .id(dtoIn.getId())
                .name(dtoIn.getName())
                .email(dtoIn.getEmail())
                .build();
    }

    @Override
    public List<UserDtoOut> toDto(List<User> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }

    public UserDtoShort toDtoShort(User user) {
        if (user == null) {
            return null;
        }
        return UserDtoShort.builder()
                .id(user.getId())
                .build();
    }
}
