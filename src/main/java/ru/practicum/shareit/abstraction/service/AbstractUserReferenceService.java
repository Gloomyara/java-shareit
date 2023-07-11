package ru.practicum.shareit.abstraction.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.abstraction.model.UserReference;
import ru.practicum.shareit.abstraction.service.AbstractService;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.user.ObjectOwnerException;
import ru.practicum.shareit.exceptions.user.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Map;

public abstract class AbstractUserReferenceService<E extends UserReference>
        extends AbstractService<E> {

    private final JpaRepository<E, Long> objectRepository;

    protected AbstractUserReferenceService(UserRepository userRepository,
                                           ObjectMapper objectMapper,
                                           JpaRepository<E, Long> objectRepository) {
        super(userRepository, objectMapper);
        this.objectRepository = objectRepository;
    }

    public E findUserReferenceById(Long objectId) {
        E e = objectRepository.findById(objectId).orElseThrow(() -> new EntityNotFoundException(objectId, "Object"));
        return e;
    }

    public E createUserReference(E e, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        e.setUserReference(user);
        return objectRepository.save(e);
    }

    public E updateUserReference(E e, Long userId) {
        return createUserReference(e, userId);
    }

    public E patchUserReference(Long objectId,
                                Map<String, Object> newFields) {
        E oldE = objectRepository.findById(objectId)
                .orElseThrow(() -> new EntityNotFoundException(objectId, "Object"));
        E newE = tryUpdateFields(oldE, newFields);
        return objectRepository.save(newE);
    }

    public boolean userExistsById(Long userId) {
        return userRepository.existsById(userId);
    }

    public void checkObjectOwner(Long objectId, Long userId) {
        E e = objectRepository.findById(objectId)
                .orElseThrow(() -> new EntityNotFoundException(objectId, "Object"));
        if (!e.getUserReference().getId().equals(userId)) {
            throw new ObjectOwnerException("Error! User id:" + userId + " is not own the object id: " + objectId);
        }
    }

    protected void checkUserId(Long userId) {
        if (!userExistsById(userId)) {
            throw new UserNotFoundException(userId);
        }
    }
}
