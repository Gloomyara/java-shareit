package ru.practicum.shareit.abstraction.userreference.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.abstraction.mapper.ModelMapper;
import ru.practicum.shareit.abstraction.model.Identified;
import ru.practicum.shareit.abstraction.model.UserReference;
import ru.practicum.shareit.abstraction.service.AbstractService;
import ru.practicum.shareit.abstraction.userreference.repository.UserReferenceRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.user.ObjectOwnerException;
import ru.practicum.shareit.exceptions.user.UserNotFoundException;

import java.util.List;
import java.util.Map;

@Slf4j
public abstract class AbstractUserReferenceEntityService<I extends Identified, O, E extends UserReference>
        extends AbstractService<I, O, E> {

    private final UserReferenceRepository<E> objectRepository;

    protected AbstractUserReferenceEntityService(ModelMapper<I, O, E> mapper,
                                                 UserRepository userRepository,
                                                 ObjectMapper objectMapper,
                                                 UserReferenceRepository<E> objectRepository) {
        super(mapper, userRepository, objectMapper);
        this.objectRepository = objectRepository;
    }

    public E findUserReferenceById(Long objectId) {
        E e = objectRepository.findById(objectId).orElseThrow(() -> new EntityNotFoundException(objectId, "Object"));
        log.debug("Find object by id, {}.", e);
        return e;
    }

    public List<E> findAllUserReferenceByUserId(Long userId) {
        List<E> list = objectRepository.findAllByUserId(userId);
        log.debug("Find all objects by UserId: {}; list = {}.", userId, list);
        return list;
    }

    public E createUserReference(E e, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        e.setUser(user);
        E entity = objectRepository.save(e);
        log.debug("Update User: id = {}.", entity.getId());
        return entity;
    }

    public E updateUserReference(E e, Long userId) {
        return createUserReference(e, userId);
    }

    public E patchUserReference(Long objectId,
                                Map<String, Object> newFields) {
        E oldE = objectRepository.findById(objectId).orElseThrow(EntityNotFoundException::new);
        E newE = tryUpdateFields(oldE, newFields);
        E updated = objectRepository.save(newE);
        log.debug("Update User: {}.", updated);
        return updated;
    }

    public void delete(Long objectId, Long userId) {
        objectRepository.deleteByUserId(userId);
        log.debug("delete object: id = {}.", objectId);
    }

    public boolean userExistsById(Long userId) {
        return userRepository.existsById(userId);
    }

    public void ObjectOwnerCheck(Long objectId, Long userId) {
        E e = objectRepository.findById(objectId).orElseThrow(EntityNotFoundException::new);
        if (!e.getUser().getId().equals(userId)) {
            throw new ObjectOwnerException("Error! User id:" + userId + " is not own the object id: " + objectId);
        }
    }
}
