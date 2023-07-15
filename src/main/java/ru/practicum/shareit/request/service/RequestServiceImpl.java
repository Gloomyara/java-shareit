package ru.practicum.shareit.request.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.abstraction.service.AbstractService;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.user.UserNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.RequestDtoIn;
import ru.practicum.shareit.request.dto.RequestDtoOut;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.pager.PageRequester;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RequestServiceImpl extends AbstractService<RequestDtoIn, RequestDtoOut, Request>
        implements RequestService {
    private final RequestMapper requestMapper;
    private final RequestRepository repository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    public RequestServiceImpl(RequestRepository repository,
                              ObjectMapper objectMapper,
                              RequestMapper requestMapper,
                              UserRepository userRepository,
                              ItemMapper itemMapper) {
        super(objectMapper);
        this.repository = repository;
        this.requestMapper = requestMapper;
        this.userRepository = userRepository;
        this.itemMapper = itemMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public RequestDtoOut findById(Long id, Long authorId) {
        checkUserId(authorId);
        return toDto(repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id, "Request")));
    }

    @Override
    public RequestDtoOut create(RequestDtoIn dtoIn, Long authorId) {
        User user = userRepository.findById(authorId)
                .orElseThrow(() -> new UserNotFoundException(authorId));
        Request request = toEntity(dtoIn);
        request.setCreated(LocalDateTime.now());
        request.setAuthor(user);
        return toDto(repository.save(request));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDtoOut> findAllByAuthorId(Long authorId) {
        checkUserId(authorId);
        return toDto(repository.findAllByAuthorIdWithItems(authorId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDtoOut> findAll(Integer from, Integer limit, Long authorId) {
        checkUserId(authorId);
        Pageable orderByCreatedDesc = new PageRequester(from, limit, Sort.by("created").descending());
        return toDto(repository.findAllOtherRequests(authorId, orderByCreatedDesc).toList());
    }

    private void checkUserId(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
    }

    @Override
    public Request toEntity(RequestDtoIn dtoIn) {
        if (dtoIn == null) return null;
        return requestMapper.toEntity(dtoIn);
    }

    @Override
    public RequestDtoOut toDto(Request entity) {
        if (entity == null) return null;
        RequestDtoOut requestDtoOut = requestMapper.toDto(entity);
        requestDtoOut.setItems(itemMapper.toDtoShort(entity.getItems()));
        return requestDtoOut;
    }

    @Override
    public List<RequestDtoOut> toDto(List<Request> dtoInList) {
        if (dtoInList == null) {
            return null;
        }
        return dtoInList.stream().map(this::toDto).collect(Collectors.toList());
    }
}
