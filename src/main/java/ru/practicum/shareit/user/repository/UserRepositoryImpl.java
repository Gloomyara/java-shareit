package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.EntityAlreadyExistException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private Long lastId = 1L;
    private final Set<String> emails = new HashSet<>();

    @Override
    public void containsOrElseThrow(long id) {
        if (!users.containsKey(id)) {
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
            throw new EntityAlreadyExistException("Error! Email: " + user.getEmail() +
                    " already registered");
        }
        user.setId(lastId++);
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        return user;
    }

    @Override
    public User patch(User user) {
        emails.remove(users.get(user.getId()).getEmail());
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        return user;
    }

    @Override
    public User delete(Long id) {
        emails.remove(users.get(id).getEmail());
        return users.remove(id);
    }
}
