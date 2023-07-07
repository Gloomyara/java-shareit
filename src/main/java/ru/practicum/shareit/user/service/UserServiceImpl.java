package ru.practicum.shareit.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.abstraction.service.AbstractService;
import ru.practicum.shareit.exceptions.user.EmailAlreadyRegisteredException;
import ru.practicum.shareit.exceptions.user.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl extends AbstractService<UserDto, UserDto, User> implements UserService {

    public UserServiceImpl(UserMapper mapper,
                           UserRepository userRepository,
                           ObjectMapper objectMapper) {
        super(mapper, userRepository, objectMapper);
    }

    @Transactional(readOnly = true)
    public UserDto findById(Long id) {
        return toDto(userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id)));
    }

    @Transactional
    public UserDto create(UserDto userDTO) {
        return toDto(userRepository.save(dtoToEntity(userDTO)));
    }

    @Transactional
    public UserDto update(UserDto userDto) {
        checkUserId(userDto.getId());
        return toDto(userRepository.save(dtoToEntity(userDto)));
    }

    @Transactional
    public UserDto patch(Long id, Map<String, Object> updatedFields) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        checkUserEmail(String.valueOf(updatedFields.get("email")), user);
        return toDto(userRepository.save(tryUpdateFields(user, updatedFields)));
    }

    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
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
}
