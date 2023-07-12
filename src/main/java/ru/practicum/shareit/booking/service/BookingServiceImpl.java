package ru.practicum.shareit.booking.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.abstraction.model.DtoIn;
import ru.practicum.shareit.abstraction.service.AbstractUserReferenceService;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.state.State;
import ru.practicum.shareit.booking.state.searcher.SearchByStateFactory;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.booking.BookingAlreadyApprovedException;
import ru.practicum.shareit.exceptions.booking.BookingAlreadyRegisteredException;
import ru.practicum.shareit.exceptions.booking.RentTimeConstraintException;
import ru.practicum.shareit.exceptions.item.ItemNotAvailableException;
import ru.practicum.shareit.exceptions.user.ObjectOwnerException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl extends AbstractUserReferenceService<Booking>
        implements BookingService {

    private final BookingMapper bookingMapper;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final SearchByStateFactory searchByStateFactory;


    protected BookingServiceImpl(BookingMapper bookingMapper,
                                 UserRepository userRepository,
                                 ObjectMapper objectMapper,
                                 ItemMapper itemMapper,
                                 UserMapper userMapper,
                                 BookingRepository bookingRepository,
                                 ItemRepository itemRepository,
                                 SearchByStateFactory searchByStateFactory) {
        super(userRepository, objectMapper, bookingRepository);
        this.bookingMapper = bookingMapper;
        this.itemMapper = itemMapper;
        this.userMapper = userMapper;
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.searchByStateFactory = searchByStateFactory;
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDtoOut findById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findByIdWithBookerAndItem(bookingId)
                .orElseThrow(() -> new EntityNotFoundException(bookingId, "Booking"));
        checkBookingAccess(booking, userId);
        return toDto(booking);
    }

    @Override
    public BookingDtoOut findById(Long objectId) {
        return toDto(findUserReferenceById(objectId));
    }

    @Override
    public List<BookingDtoOut> findAllByUserId(Long userId) {
        checkUserId(userId);
        return toDto(bookingRepository.findAllByBookerId(userId));
    }

    @Override
    @Transactional
    public BookingDtoOut create(DtoIn in, Long userId) {
        BookingDtoIn bookingDtoIn = (BookingDtoIn) in;
        validateRentTime(bookingDtoIn);
        checkUserId(userId);
        Item item = itemRepository.findByIdWithOwner(bookingDtoIn.getItemId())
                .orElseThrow(() -> new EntityNotFoundException(bookingDtoIn.getItemId(), "Item"));
        checkItemAvailable(item);
        checkBookerIsNotItemOwner(item.getUserReference().getId(), userId);
        checkBookingExists(item, userId);
        Booking booking = dtoToEntity(bookingDtoIn);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        return toDto(super.createUserReference(booking, userId));
    }

    @Override
    public BookingDtoOut update(DtoIn in, Long userId) {
        checkUserId(userId);
        return toDto(updateUserReference(dtoToEntity(in), userId));
    }

    @Override
    public BookingDtoOut patch(Long id, Map<String, Object> fields, Long userId) {
        checkObjectOwner(id, userId);
        return toDto(patchUserReference(id, fields));
    }

    @Override
    @Transactional
    public BookingDtoOut patch(Long bookingId, Long userId, Boolean approved) {
        checkUserId(userId);
        Booking booking = bookingRepository.findByIdWithBookerAndItem(bookingId)
                .orElseThrow(() -> new EntityNotFoundException(bookingId, "Booking"));
        checkBookingStatus(booking);
        checkItemOwner(booking.getItem().getUserReference().getId(), userId);
        boolean isApproved = Boolean.TRUE.equals(approved);
        if (isApproved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return toDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoOut> findAllByOwnerId(Long ownerId, State state) {
        checkUserId(ownerId);
        return toDto(searchByStateFactory.searchByState(state).findAllByOwnerId(ownerId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoOut> findAllByBookerId(Long userId, State state) {
        checkUserId(userId);
        return toDto(searchByStateFactory.searchByState(state).findAllByBookerId(userId));
    }

    private void validateRentTime(BookingDtoIn dtoIn) {
        boolean b1 = dtoIn.getStart().isAfter(dtoIn.getEnd());
        boolean b2 = dtoIn.getStart().equals(dtoIn.getEnd());
        if (b1 || b2) {
            throw new RentTimeConstraintException("Error! Item rent start is after rent end.");
        }
    }

    private void checkItemAvailable(Item item) {
        if (Boolean.FALSE.equals(item.getAvailable())) {
            throw new ItemNotAvailableException("Error! Cannot create booking for unavailable item. " + item);
        }
    }

    private void checkItemOwner(Long id, Long userId) {
        if (!Objects.equals(id, userId)) {
            throw new ObjectOwnerException(userId, "item");
        }
    }

    private void checkBookerIsNotItemOwner(Long id, Long userId) {
        if (Objects.equals(id, userId)) {
            throw new ObjectOwnerException("Error! Cannot create booking, user id:" + id + " is item owner.");
        }
    }

    private void checkBookingExists(Item item, Long userId) {
        if (bookingRepository.findBookingByBookerIdAndItemIdAndStatus(userId, item.getId(), Status.WAITING).isPresent()) {
            throw new BookingAlreadyRegisteredException("Error! Booking already registered.");
        }
    }

    private void checkBookingAccess(Booking booking, Long userId) {
        boolean b1 = !booking.getItem().getUserReference().getId().equals(userId);
        boolean b2 = !booking.getBooker().getId().equals(userId);
        if (b1 && b2) {
            throw new ObjectOwnerException(userId, "booking or item");
        }
    }

    private void checkBookingStatus(Booking booking) {
        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new BookingAlreadyApprovedException("Error! Booking id: " + booking.getId() + " is already approved.");
        }
    }

    @Override
    public Booking dtoToEntity(DtoIn in) {
        return bookingMapper.dtoToEntity(in);
    }

    @Override
    public BookingDtoOut toDto(Booking booking) {
        BookingDtoOut bookingDtoOut = bookingMapper.toDto(booking);
        bookingDtoOut.setBooker(userMapper.toDtoShort(booking.getBooker()));
        bookingDtoOut.setItem(itemMapper.toDtoShort(booking.getItem()));
        return bookingDtoOut;
    }

    @Override
    public List<BookingDtoOut> toDto(List<Booking> listIn) {
        return listIn.stream().map(this::toDto).collect(Collectors.toList());
    }
}
