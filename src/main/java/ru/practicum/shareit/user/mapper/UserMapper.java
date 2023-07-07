package ru.practicum.shareit.user.mapper;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.abstraction.mapper.AbstractModelMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoShort;
import ru.practicum.shareit.user.model.User;

@Service
public class UserMapper extends AbstractModelMapper<UserDto, UserDto, User> {

    @Override
    public UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    @Override
    public User dtoToEntity(UserDto userDTO) {
        return User.builder()
                .id(userDTO.getId())
                .name(userDTO.getName())
                .email(userDTO.getEmail())
                .build();
    }

    public UserDtoShort toDtoShort(User user) {
        return UserDtoShort.builder()
                .id(user.getId())
                .build();
    }
}
