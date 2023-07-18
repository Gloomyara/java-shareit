package ru.practicum.shareit.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.abstraction.service.AbstractService;
import ru.practicum.shareit.exceptions.user.EmailAlreadyRegisteredException;
import ru.practicum.shareit.exceptions.user.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDtoIn;
import ru.practicum.shareit.user.dto.UserDtoOut;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class UserServiceImpl extends AbstractService<UserDtoIn, UserDtoOut, User>
        implements UserService {
    private final UserMapper userMapper;
    private final UserRepository repository;

    public UserServiceImpl(UserRepository repository,
                           ObjectMapper objectMapper,
                           UserMapper userMapper) {
        super(objectMapper);
        this.repository = repository;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDtoOut findById(Long id) {
        return toDto(repository.findById(id).orElseThrow(() -> new UserNotFoundException(id)));
    }

    @Override
    public UserDtoOut create(UserDtoIn dtoIn) {
        return toDto(repository.save(toEntity(dtoIn)));
    }

    @Override
    public UserDtoOut update(UserDtoIn dtoIn) {
        checkUserId(dtoIn.getId());
        return toDto(repository.save(toEntity(dtoIn)));
    }

    @Override
    public UserDtoOut patch(Long id, Map<String, Object> fields) {
        checkUserEmail(id, String.valueOf(fields.get("email")));
        User user = repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        return toDto(repository.save(tryUpdateFields(user, fields)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDtoOut> findAll() {
        return toDto(repository.findAll());
    }

    @Override
    public void delete(Long id) {
        checkUserId(id);
        repository.deleteById(id);
    }

    private void checkUserId(Long id) {
        if (!repository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
    }

    private void checkUserEmail(Long userId, String email) {
        if (repository.existsByEmailAndIdNot(email, userId)) {
            throw new EmailAlreadyRegisteredException("Error! Email: " + email + " already registered.");
        }
    }

    @Override
    public User toEntity(UserDtoIn dtoIn) {
        if (dtoIn == null) return null;
        return userMapper.toEntity(dtoIn);
    }

    @Override
    public UserDtoOut toDto(User entity) {
        if (entity == null) return null;
        return userMapper.toDto(entity);
    }

    @Override
    public List<UserDtoOut> toDto(List<User> dtoInList) {
        if (dtoInList == null) return Collections.emptyList();
        return userMapper.toDto(dtoInList);
    }
}
