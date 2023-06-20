package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;

@Data
public class UserDto {
    @Email
    private String email;
    private String name;
}