package ru.practicum.shareit.booking.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.abstraction.service.AbstractService;
import ru.practicum.shareit.booking.BookingDtoIn;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.state.searcher.SearchByStateFactory;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.booking.BookingAlreadyApprovedException;
import ru.practicum.shareit.exceptions.item.ItemNotAvailableException;
import ru.practicum.shareit.exceptions.user.ObjectOwnerException;
import ru.practicum.shareit.exceptions.user.UserNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.pager.PageRequester;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.util.UtilConstants.BOOKING_SORT;

@Service
@Transactional
public class BookingServiceImpl extends AbstractService<BookingDtoIn, BookingDtoOut, Booking>
        implements BookingService {
    private final BookingMapper bookingMapper;
    private final ItemMapper itemMapper;
    private final BookingRepository repository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final SearchByStateFactory searchByStateFactory;


    public BookingServiceImpl(ObjectMapper objectMapper,
                              BookingRepository repository,
                              ItemRepository itemRepository,
                              UserRepository userRepository,
                              SearchByStateFactory searchByStateFactory,
                              BookingMapper bookingMapper,
                              ItemMapper itemMapper) {
        super(objectMapper);
        this.bookingMapper = bookingMapper;
        this.itemMapper = itemMapper;
        this.repository = repository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.searchByStateFactory = searchByStateFactory;
    }

    @Transactional(readOnly = true)
    public BookingDtoOut findById(Long bookingId, Long userId) {
        checkBookingAccess(bookingId, userId);
        return toDto(repository.findByIdWithBookerAndItem(bookingId)
                .orElseThrow(() -> new EntityNotFoundException(bookingId, "Booking")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoOut> findAllByItemOwnerId(Integer from, Integer limit, Long ownerId, State state) {
        checkUserId(ownerId);
        return toDto(searchByStateFactory.searchByState(state)
                .findAllByItemOwnerId(ownerId, new PageRequester(from, limit, BOOKING_SORT)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoOut> findAllByBookerId(Integer from, Integer limit, Long bookerId, State state) {
        checkUserId(bookerId);
        return toDto(searchByStateFactory.searchByState(state)
                .findAllByBookerId(bookerId, new PageRequester(from, limit, BOOKING_SORT)));
    }

    @Override
    public BookingDtoOut save(BookingDtoIn dtoIn, Long bookerId) {
        checkItemAvailable(dtoIn.getItemId());
        checkBookerIsNotItemOwner(dtoIn.getItemId(), bookerId);
        return toDto(repository.save(mergeToBookingWithStatusWaiting(dtoIn, bookerId)));
    }

    @Override
    public BookingDtoOut update(BookingDtoIn dtoIn, Long bookerId) {
        checkUserId(bookerId);
        checkBookerAccess(dtoIn.getId(), bookerId);
        checkBookerIsNotItemOwner(dtoIn.getItemId(), bookerId);
        return toDto(repository.save(mergeToBookingWithStatusWaiting(dtoIn, bookerId)));
    }

    @Override
    public BookingDtoOut patch(Long bookingId, Long itemOwnerId, boolean approved) {
        checkItemOwnerAccess(bookingId, itemOwnerId);
        checkBookingStatus(bookingId);
        Booking booking = repository.findByIdWithBookerAndItem(bookingId)
                .orElseThrow(() -> new EntityNotFoundException(bookingId, "Booking"));
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return toDto(repository.save(booking));
    }

    private Booking mergeToBookingWithStatusWaiting(BookingDtoIn dtoIn, Long bookerId) {
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new UserNotFoundException(bookerId));
        Item item = itemRepository.findById(dtoIn.getItemId())
                .orElseThrow(() -> new EntityNotFoundException(dtoIn.getItemId(), "Item"));
        Booking booking = toEntity(dtoIn);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        return booking;
    }

    private void checkUserId(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
    }

    private void checkBookerAccess(Long bookingId, Long userId) {
        if (!repository.existsByIdAndBookerId(bookingId, userId)) {
            throw new ObjectOwnerException("Error! User id:" + userId +
                    " is not Booking id: " + bookingId + " booker.");
        }
    }

    private void checkItemOwnerAccess(Long bookingId, Long userId) {
        if (!repository.existsByIdAndItemOwnerId(bookingId, userId)) {
            throw new ObjectOwnerException("Error! User id:" + userId +
                    " is not Item id: " + bookingId + " owner.");
        }
    }

    private void checkItemAvailable(Long itemId) {
        if (itemRepository.existsByIdAndAvailableIsFalse(itemId)) {
            throw new ItemNotAvailableException("Error! Cannot create booking for unavailable item.");
        }
    }

    private void checkBookerIsNotItemOwner(Long itemId, Long userId) {
        if (itemRepository.existsByIdAndOwnerId(itemId, userId)) {
            throw new ObjectOwnerException("Error! Cannot create booking, user id:" + userId + " is item owner.");
        }
    }

    private void checkBookingAccess(Long bookingId, Long userId) {
        if (!repository.existsByBookingIdAndBookerOrItemOwnerId(bookingId, userId)) {
            throw new ObjectOwnerException(userId, "booking or item");
        }
    }

    private void checkBookingStatus(Long bookingId) {
        if (!repository.existsByIdAndStatus(bookingId, Status.WAITING)) {
            throw new BookingAlreadyApprovedException("Error! Booking id: " + bookingId + " is already approved.");
        }
    }

    @Override
    public Booking toEntity(BookingDtoIn dtoIn) {
        if (dtoIn == null) return null;
        return bookingMapper.toEntity(dtoIn);
    }

    @Override
    public BookingDtoOut toDto(Booking entity) {
        if (entity == null) return null;
        BookingDtoOut bookingDtoOut = bookingMapper.toDto(entity);
        bookingDtoOut.setItem(itemMapper.toDtoShort(entity.getItem()));
        return bookingDtoOut;
    }

    @Override
    public List<BookingDtoOut> toDto(List<Booking> dtoInList) {
        if (dtoInList == null) {
            return null;
        }
        return dtoInList.stream().map(this::toDto).collect(Collectors.toList());
    }
}
