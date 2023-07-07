package ru.practicum.shareit.booking.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.abstraction.userreference.service.AbstractUserReferenceService;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.state.State;
import ru.practicum.shareit.booking.state.searcher.SearchByStateFactory;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.exceptions.booking.BookingAlreadyApprovedException;
import ru.practicum.shareit.exceptions.booking.BookingAlreadyRegisteredException;
import ru.practicum.shareit.exceptions.booking.RentTimeConstraintException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.item.ItemNotAvailableException;
import ru.practicum.shareit.exceptions.user.ObjectOwnerException;

import java.util.List;
import java.util.Objects;

@Service
public class BookingServiceImpl extends AbstractUserReferenceService<BookingDtoIn, BookingDtoOut, Booking>
        implements BookingService {
    private final BookingRepository bookingRepository;

    private final ItemRepository itemRepository;
    private final SearchByStateFactory searchByStateFactory;


    protected BookingServiceImpl(BookingMapper mapper,
                                 UserRepository userRepository,
                                 ObjectMapper objectMapper,
                                 BookingRepository bookingRepository,
                                 ItemRepository itemRepository,
                                 SearchByStateFactory searchByStateFactory) {
        super(mapper, userRepository, objectMapper, bookingRepository);
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.searchByStateFactory = searchByStateFactory;
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDtoOut findById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findByIdWithUserAndItem(bookingId)
                .orElseThrow(() -> new EntityNotFoundException(bookingId, "Booking"));
        checkBookingAccess(booking, userId);
        return toDto(booking);
    }

    @Override
    @Transactional
    public BookingDtoOut create(BookingDtoIn bookingDtoIn, Long userId) {
        validateRentTime(bookingDtoIn);
        checkUserId(userId);
        Item item = itemRepository.findByIdWithUser(bookingDtoIn.getItemId())
                .orElseThrow(() -> new EntityNotFoundException(bookingDtoIn.getItemId(), "Item"));
        checkItemAvailable(item);
        checkBookerIsNotItemOwner(item.getUser().getId(), userId);
        checkBookingExists(item, userId);
        Booking booking = dtoToEntity(bookingDtoIn);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        return toDto(super.createUserReference(booking, userId));
    }

    @Override
    @Transactional
    public BookingDtoOut patch(Long bookingId, Long userId, Boolean approved) {
        checkUserId(userId);
        Booking booking = bookingRepository.findByIdWithUserAndItem(bookingId)
                .orElseThrow(() -> new EntityNotFoundException(bookingId, "Booking"));
        checkBookingStatus(booking);
        checkItemOwner(booking.getItem().getUser().getId(), userId);
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
        if (bookingRepository.findBookingByUserIdAndItemIdAndStatus(userId, item.getId(), Status.WAITING).isPresent()) {
            throw new BookingAlreadyRegisteredException("Error! Booking already registered.");
        }
    }

    private void checkBookingAccess(Booking booking, Long userId) {
        boolean b1 = !booking.getItem().getUser().getId().equals(userId);
        boolean b2 = !booking.getUser().getId().equals(userId);
        if (b1 && b2) {
            throw new ObjectOwnerException(userId, "booking or item");
        }
    }

    private void checkBookingStatus(Booking booking) {
        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new BookingAlreadyApprovedException("Error! Booking id: " + booking.getId() + " is already approved.");
        }
    }
}
