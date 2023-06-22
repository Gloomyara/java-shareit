package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.EntityAlreadyExistException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private final AtomicLong lastId = new AtomicLong(1);
    private final Set<String> emails = new HashSet<>();
    private final ItemRepository itemRepository;

    @Override
    public void containsOrElseThrow(long id) {
        if (!users.containsKey(id)) {
            log.warn("User with Id: {} not found", id);
            throw new EntityNotFoundException("User with Id: " + id + " not found");
        }
    }

    @Override
    public Collection<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User create(User user) {
        if (emails.contains(user.getEmail())) {
            log.warn("Email: {} already used", user.getEmail());
            throw new EntityAlreadyExistException("Error! Email: " + user.getEmail() +
                    " already used");
        }
        user.setId(lastId.getAndIncrement());
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        return user;
    }

    @Override
    public Optional<User> getById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User patch(Long id, UserDto userDto) {
        User user = users.get(id);
        if (userDto.getEmail() != null) {
            String oldEmail = user.getEmail();
            emails.remove(oldEmail);
            if (emails.contains(userDto.getEmail())) {
                emails.add(oldEmail);
                log.warn("Email: {} already used", user.getEmail());
                throw new EntityAlreadyExistException(
                        "Error! Email: " + userDto.getEmail() +
                                " already used");
            }
            emails.add(userDto.getEmail());
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            user.setName(userDto.getName());
        }
        return user;
    }

    @Override
    public void delete(Long id) {
        emails.remove(users.get(id).getEmail());
        itemRepository.deleteByUserId(id);
        users.remove(id);
    }
}
