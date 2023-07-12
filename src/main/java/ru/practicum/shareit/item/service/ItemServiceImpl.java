package ru.practicum.shareit.item.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.abstraction.model.DtoIn;
import ru.practicum.shareit.abstraction.service.AbstractUserReferenceService;
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
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ItemServiceImpl extends AbstractUserReferenceService<Item> implements ItemService {

    private final ItemMapper itemMapper;
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final CommentMapper commentMapper;
    private final BookingMapper bookingMapper;

    protected ItemServiceImpl(ItemMapper itemMapper,
                              UserRepository userRepository,
                              ObjectMapper objectMapper,
                              ItemRepository itemRepository,
                              CommentRepository commentRepository,
                              BookingRepository bookingRepository,
                              CommentMapper commentMapper,
                              BookingMapper bookingMapper) {
        super(userRepository, objectMapper, itemRepository);
        this.itemMapper = itemMapper;
        this.itemRepository = itemRepository;
        this.commentRepository = commentRepository;
        this.bookingRepository = bookingRepository;
        this.commentMapper = commentMapper;
        this.bookingMapper = bookingMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDtoOut findById(Long itemId, Long userId) {
        checkUserId(userId);
        Item item = itemRepository.findByIdWithOwnerAndComments(itemId)
                .orElseThrow(() -> new EntityNotFoundException(itemId, "Item"));
        ItemDtoOut itemDtoOut = toDto(item);
        boolean isOwner = Objects.equals(item.getUserReference().getId(), userId);
        if (isOwner) {
            bookingRepository.findTopByItemIdAndStatusAndStartBeforeOrderByStartDesc(
                            itemId, Status.APPROVED, LocalDateTime.now())
                    .ifPresent(b -> itemDtoOut.setLastBooking(toDtoShort(b)));
            bookingRepository.findTopByItemIdAndStatusAndStartAfterOrderByStart(
                            itemId, Status.APPROVED, LocalDateTime.now())
                    .ifPresent(b -> itemDtoOut.setNextBooking(toDtoShort(b)));
        }
        if (Objects.nonNull(item.getComments())) {
            itemDtoOut.setComments(commentMapper.toDto(item.getComments()));
        }
        return itemDtoOut;
    }

    @Override
    public ItemDtoOut findById(Long objectId) {
        return toDto(findUserReferenceById(objectId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDtoOut> findAllByOwnerId(Long userId) {
        checkUserId(userId);
        List<Item> items = itemRepository.findAllByOwnerIdWithComments(userId);
        List<BookingShort> lastBookings = bookingRepository.findLastBookingsByOwnerId(userId);
        List<BookingShort> nextBookings = bookingRepository.findNextBookingsByOwnerId(userId);
        return mergeToItemDtoOut(items, lastBookings, nextBookings);
    }

    @Override
    public ItemDtoOut create(DtoIn in, Long userId) {
        checkUserId(userId);
        return toDto(createUserReference(dtoToEntity(in), userId));
    }

    @Override
    public ItemDtoOut update(DtoIn in, Long userId) {
        checkUserId(userId);
        return toDto(updateUserReference(dtoToEntity(in), userId));
    }

    @Override
    public ItemDtoOut patch(Long id, Map<String, Object> fields, Long userId) {
        checkObjectOwner(id, userId);
        return toDto(patchUserReference(id, fields));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDtoOut> searchByText(String text) {
        return toDto(itemRepository
                .findAllByNameContainsIgnoreCaseOrDescriptionContainsIgnoreCaseAndAvailableIsTrue(text, text));
    }

    @Override
    @Transactional
    public CommentDtoOut createComment(Long itemId, Long userId, CommentDtoIn commentDtoIn) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        Item item = itemRepository.findByIdWithOwner(itemId)
                .orElseThrow(() -> new EntityNotFoundException(itemId, "Item"));
        commentCheck(userId, item);
        Comment comment = commentMapper.dtoToEntity(commentDtoIn);
        comment.setAuthor(user);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
        item.getComments().add(comment);
        return toDto(commentRepository.save(comment));
    }


    private void commentCheck(Long userId, Item item) {
        if (!bookingRepository.existsBookingByItemIdAndBookerId(item.getId(), userId)) {
            throw new UnregisteredBookingException("Error can't add comment! User id:" + userId +
                    " have no registered booking to the item.");
        }
        if (item.getUserReference().getId().equals(userId)) {
            throw new ObjectOwnerException("Error can't add comment! User id:" + userId + " is item owner.");
        }
    }

    private ArrayList<ItemDtoOut> mergeToItemDtoOut(
            List<Item> items,
            List<BookingShort> lastBookings,
            List<BookingShort> nextBookings) {
        Map<Long, ItemDtoOut> map = new HashMap<>();
        for (Item item : items) {
            ItemDtoOut itemDtoOut = toDto(item);
            itemDtoOut.setComments(commentMapper.toDto(item.getComments()));
            map.put(item.getId(), itemDtoOut);
        }
        lastBookings.forEach(last -> map.get(last.getItemId()).setLastBooking(toDtoShort(last)));
        nextBookings.forEach(next -> map.get(next.getItemId()).setNextBooking(toDtoShort(next)));
        return new ArrayList<>(map.values());
    }

    private CommentDtoOut toDto(Comment comment) {
        return commentMapper.toDto(comment);
    }

    private BookingDtoShort toDtoShort(BookingShort bookingShort) {
        return bookingMapper.toDtoShort(bookingShort);
    }

    @Override
    public Item dtoToEntity(DtoIn in) {
        return itemMapper.dtoToEntity(in);
    }

    @Override
    public ItemDtoOut toDto(Item item) {
        return itemMapper.toDto(item);
    }

    @Override
    public List<ItemDtoOut> toDto(List<Item> listIn) {
        return itemMapper.toDto(listIn);
    }
}
