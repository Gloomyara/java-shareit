package ru.practicum.shareit.item.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.abstraction.service.AbstractService;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.item.UnregisteredBookingException;
import ru.practicum.shareit.exceptions.user.ObjectOwnerException;
import ru.practicum.shareit.exceptions.user.UserNotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.dto.CommentDtoIn;
import ru.practicum.shareit.item.comment.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.pager.PageRequester;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.util.UtilConstants.ITEM_SORT;

@Service
@Transactional
public class ItemServiceImpl extends AbstractService<ItemDtoIn, ItemDtoOut, Item>
        implements ItemService {
    private final ItemRepository repository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final RequestRepository requestRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final BookingMapper bookingMapper;

    public ItemServiceImpl(ItemRepository repository,
                           ObjectMapper objectMapper,
                           UserRepository userRepository,
                           CommentRepository commentRepository,
                           BookingRepository bookingRepository,
                           RequestRepository requestRepository,
                           ItemMapper itemMapper,
                           CommentMapper commentMapper,
                           BookingMapper bookingMapper) {
        super(objectMapper);
        this.itemMapper = itemMapper;
        this.commentMapper = commentMapper;
        this.bookingMapper = bookingMapper;
        this.repository = repository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.bookingRepository = bookingRepository;
        this.requestRepository = requestRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDtoOut findById(Long itemId, Long ownerId) {
        checkUserId(ownerId);
        Item item = repository.findByIdWithOwnerAndComments(itemId)
                .orElseThrow(() -> new EntityNotFoundException(itemId, "Item"));
        ItemDtoOut itemDtoOut = toDto(item);
        boolean isOwner = Objects.equals(item.getOwner().getId(), ownerId);
        if (isOwner) {
            bookingRepository.findTopByItemIdAndStatusAndStartBeforeOrderByStartDesc(
                            itemId, Status.APPROVED, LocalDateTime.now())
                    .ifPresent(b -> itemDtoOut.setLastBooking(toBookingDtoShort(b)));
            bookingRepository.findTopByItemIdAndStatusAndStartAfterOrderByStart(
                            itemId, Status.APPROVED, LocalDateTime.now())
                    .ifPresent(b -> itemDtoOut.setNextBooking(toBookingDtoShort(b)));
        }
        return itemDtoOut;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDtoOut> findAllByOwnerId(Integer from, Integer limit, Long ownerId) {
        checkUserId(ownerId);
        List<Item> items = repository.findAllByOwnerIdWithComments(ownerId,
                new PageRequester(from, limit, ITEM_SORT)).toList();
        List<BookingShort> lastBookings = bookingRepository.findLastBookingsByItemOwnerId(ownerId);
        List<BookingShort> nextBookings = bookingRepository.findNextBookingsByItemOwnerId(ownerId);
        return mergeToDtoOut(items, lastBookings, nextBookings);
    }

    @Override
    public ItemDtoOut create(ItemDtoIn dtoIn, Long ownerId) {
        checkUserId(ownerId);
        return toDto(repository.save(mergeToEntity(dtoIn, ownerId)));
    }

    @Override
    public ItemDtoOut update(ItemDtoIn dtoIn, Long ownerId) {
        checkItemOwner(dtoIn.getId(), ownerId);
        return toDto(repository.save(mergeToEntity(dtoIn, ownerId)));
    }

    @Override
    public ItemDtoOut patch(Long itemId, Map<String, Object> fields, Long ownerId) {
        checkItemOwner(itemId, ownerId);
        Item item = repository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException(itemId, "Item"));
        return toDto(repository.save(tryUpdateFields(item, fields)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDtoOut> searchByNameOrDescription(Integer from, Integer limit, String text) {
        return toDto(repository.findAllByNameContainsIgnoreCaseOrDescriptionContainsIgnoreCaseAndAvailableIsTrue(
                text, text, new PageRequester(from, limit, ITEM_SORT)).toList());
    }

    @Override
    public CommentDtoOut createComment(Long itemId, Long authorId, CommentDtoIn dtoIn) {
        checkAuthorIsItemOwner(itemId, authorId);
        commentCheck(itemId, authorId);
        User user = userRepository.findById(authorId)
                .orElseThrow(() -> new UserNotFoundException(authorId));
        Item item = repository.findByIdWithOwner(itemId)
                .orElseThrow(() -> new EntityNotFoundException(itemId, "Item"));
        Comment comment = toComment(dtoIn);
        comment.setAuthor(user);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
        item.getComments().add(comment);
        return toCommentDto(commentRepository.save(comment));
    }

    private Item mergeToEntity(ItemDtoIn dtoIn, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        Item item = toEntity(dtoIn);
        item.setOwner(user);
        Long requestId = dtoIn.getRequestId();
        if (Objects.nonNull(requestId)) {
            Request request = requestRepository.findByIdWithItems(requestId)
                    .orElseThrow(() -> new EntityNotFoundException(requestId, "Request"));
            item.setRequest(request);
            if (Objects.isNull(request.getItems())) {
                request.setItems(List.of(item));
            } else {
                request.getItems().add(item);
            }
        }
        return item;
    }

    private List<ItemDtoOut> mergeToDtoOut(List<Item> items,
                                           List<BookingShort> lastBookings,
                                           List<BookingShort> nextBookings) {
        Map<Long, ItemDtoOut> map = new HashMap<>();
        for (Item item : items) {
            ItemDtoOut itemDtoOut = toDto(item);
            itemDtoOut.setComments(toCommentDto(item.getComments()));
            map.put(item.getId(), itemDtoOut);
        }
        lastBookings.forEach(l -> map.get(l.getItemId()).setLastBooking(toBookingDtoShort(l)));
        nextBookings.forEach(n -> map.get(n.getItemId()).setNextBooking(toBookingDtoShort(n)));
        return new ArrayList<>(map.values());
    }

    private void checkUserId(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
    }

    public void checkItemOwner(Long itemId, Long userId) {
        if (!repository.existsByIdAndOwnerId(itemId, userId)) {
            throw new ObjectOwnerException("Error! User id:" + userId +
                    " is not Item id: " + itemId + " owner.");
        }
    }

    private void commentCheck(Long itemId, Long userId) {
        if (!bookingRepository.existsBookingByItemIdAndBookerId(itemId, userId)) {
            throw new UnregisteredBookingException("Error can't add comment! User id:" + userId +
                    " have no registered booking to the item.");
        }
    }

    private void checkAuthorIsItemOwner(Long itemId, Long userId) {
        if (repository.existsByIdAndOwnerId(itemId, userId)) {
            throw new ObjectOwnerException("Error! Cannot add comment, user id:" + userId + " is item owner.");
        }
    }

    private Comment toComment(CommentDtoIn dtoIn) {
        if (dtoIn == null) return null;
        return commentMapper.toEntity(dtoIn);
    }

    private CommentDtoOut toCommentDto(Comment comment) {
        if (comment == null) return null;
        return commentMapper.toDto(comment);
    }

    private List<CommentDtoOut> toCommentDto(List<Comment> comments) {
        if (comments == null) return null;
        return commentMapper.toDto(comments);
    }

    private BookingDtoShort toBookingDtoShort(BookingShort bookingShort) {
        if (bookingShort == null) return null;
        return bookingMapper.toDtoShort(bookingShort);
    }

    @Override
    public Item toEntity(ItemDtoIn dtoIn) {
        if (dtoIn == null) return null;
        return itemMapper.toEntity(dtoIn);
    }

    @Override
    public ItemDtoOut toDto(Item entity) {
        if (entity == null) return null;
        ItemDtoOut itemDtoOut = itemMapper.toDto(entity);
        itemDtoOut.setComments(toCommentDto(entity.getComments()));
        return itemDtoOut;
    }

    @Override
    public List<ItemDtoOut> toDto(List<Item> dtoInList) {
        if (dtoInList == null) {
            return null;
        }
        return dtoInList.stream().map(this::toDto).collect(Collectors.toList());
    }
}