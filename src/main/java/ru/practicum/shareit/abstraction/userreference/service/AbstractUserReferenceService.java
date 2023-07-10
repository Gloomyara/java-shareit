package ru.practicum.shareit.abstraction.userreference.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.practicum.shareit.abstraction.mapper.ModelMapper;
import ru.practicum.shareit.abstraction.model.Identified;
import ru.practicum.shareit.abstraction.model.UserReference;
import ru.practicum.shareit.abstraction.userreference.repository.UserReferenceRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.exceptions.user.UserNotFoundException;

import java.util.List;
import java.util.Map;

public class AbstractUserReferenceService<I extends Identified, O, E extends UserReference>
        extends AbstractUserReferenceEntityService<I, O, E>
        implements UserReferenceService<I, O> {

    protected AbstractUserReferenceService(ModelMapper<I, O, E> mapper,
                                           UserRepository userRepository,
                                           ObjectMapper objectMapper,
                                           UserReferenceRepository<E> objectRepository) {
        super(mapper, userRepository, objectMapper, objectRepository);
    }

    @Override
    public O findById(Long objectId) {
        return toDto(findUserReferenceById(objectId));
    }

    @Override
    public List<O> findAllByUserId(Long userId) {
        checkUserId(userId);
        return toDto(findAllUserReferenceByUserId(userId));
    }

    @Override
    public O create(I in, Long userId) {
        checkUserId(userId);
        return toDto(createUserReference(dtoToEntity(in), userId));
    }

    @Override
    public O update(I in, Long userId) {
        checkUserId(userId);
        return toDto(updateUserReference(dtoToEntity(in), userId));
    }

    @Override
    public O patch(Long id, Map<String, Object> fields, Long userId) {
        checkObjectOwner(id, userId);
        return toDto(patchUserReference(id, fields));
    }


    protected void checkUserId(Long userId) {
        if (!userExistsById(userId)) {
            throw new UserNotFoundException(userId);
        }
    }
}
