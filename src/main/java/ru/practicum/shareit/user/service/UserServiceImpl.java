package ru.practicum.shareit.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.abstraction.model.DtoIn;
import ru.practicum.shareit.abstraction.service.AbstractService;
import ru.practicum.shareit.exceptions.user.EmailAlreadyRegisteredException;
import ru.practicum.shareit.exceptions.user.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDtoIn;
import ru.practicum.shareit.user.dto.UserDtoOut;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl extends AbstractService<User> implements UserService {
    private final UserMapper mapper;

    public UserServiceImpl(UserMapper mapper,
                           UserRepository userRepository,
                           ObjectMapper objectMapper) {
        super(userRepository, objectMapper);
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public UserDtoOut findById(Long id) {
        return toDto(userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id)));
    }

    @Transactional
    public UserDtoOut create(UserDtoIn userDTOIn) {
        return toDto(userRepository.save(dtoToEntity(userDTOIn)));
    }

    @Transactional
    public UserDtoOut update(UserDtoIn userDtoIn) {
        checkUserId(userDtoIn.getId());
        return toDto(userRepository.save(dtoToEntity(userDtoIn)));
    }

    @Transactional
    public UserDtoOut patch(Long id, Map<String, Object> fields) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        checkUserEmail(String.valueOf(fields.get("email")), user);
        return toDto(userRepository.save(tryUpdateFields(user, fields)));
    }

    @Transactional(readOnly = true)
    public List<UserDtoOut> findAll() {
        return toDto(userRepository.findAll());
    }

    @Transactional
    public void delete(Long id) {
        checkUserId(id);
        userRepository.deleteById(id);
    }

    private void checkUserId(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
    }

    private void checkUserEmail(String email, User user) {
        if (userRepository.existsByEmail(email) && !user.getEmail().equals(email)) {
            throw new EmailAlreadyRegisteredException("Error! Email: " + email + " already registered.");
        }
    }

    @Override
    public User dtoToEntity(DtoIn in) {
        return mapper.dtoToEntity(in);
    }

    @Override
    public UserDtoOut toDto(User user) {
        return mapper.toDto(user);
    }

    @Override
    public List<UserDtoOut> toDto(List<User> listIn) {
        return mapper.toDto(listIn);
    }
}
