package ru.practicum.shareit.booking;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.state.State;
import ru.practicum.shareit.booking.state.searcher.*;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.booking.BookingAlreadyApprovedException;
import ru.practicum.shareit.exceptions.booking.RentTimeConstraintException;
import ru.practicum.shareit.exceptions.booking.UnknownStateException;
import ru.practicum.shareit.exceptions.item.ItemNotAvailableException;
import ru.practicum.shareit.exceptions.user.ObjectOwnerException;
import ru.practicum.shareit.exceptions.user.UserNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.pager.PageRequester;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.util.UtilConstants.DEFAULT_FROM;
import static ru.practicum.shareit.util.UtilConstants.DEFAULT_LIMIT;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    private BookingService bookingService;

    private final EasyRandom generator = new EasyRandom();

    private final int from = Integer.parseInt(DEFAULT_FROM);
    private final int limit = Integer.parseInt(DEFAULT_LIMIT);

    @BeforeEach
    void setUp() {
        bookingService = Mockito.mock(BookingServiceImpl.class, CALLS_REAL_METHODS);
    }

    @Test
    void create_whenStartAfterEndOrEquals_assertThrowsRentTimeConstraintException() {
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        BookingDtoIn dtoIn = generator.nextObject(BookingDtoIn.class);
        dtoIn.setEnd(LocalDateTime.MIN);
        assertThrows(RentTimeConstraintException.class,
                () -> bookingService.create(dtoIn, generator.nextLong()));
        verify(bookingRepository, never())
                .save(any(Booking.class));
    }

    @Test
    void create_whenItemNotAvailable_assertThrowsItemNotAvailableException() {
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        ReflectionTestUtils.setField(bookingService, "itemRepository", itemRepository);
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        BookingDtoIn dtoIn = generator.nextObject(BookingDtoIn.class);
        dtoIn.setEnd(LocalDateTime.MAX);
        when(itemRepository.existsByIdAndAvailableIsFalse(anyLong()))
                .thenReturn(true);
        assertThrows(ItemNotAvailableException.class,
                () -> bookingService.create(dtoIn, generator.nextLong()));
        verify(bookingRepository, never())
                .save(any(Booking.class));
    }

    @Test
    void create_whenBookerItemOwner_assertThrowsObjectOwnerException() {
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        ReflectionTestUtils.setField(bookingService, "itemRepository", itemRepository);
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        BookingDtoIn dtoIn = generator.nextObject(BookingDtoIn.class);
        dtoIn.setEnd(LocalDateTime.MAX);
        when(itemRepository.existsByIdAndOwnerId(anyLong(), anyLong()))
                .thenReturn(true);
        assertThrows(ObjectOwnerException.class,
                () -> bookingService.create(dtoIn, generator.nextLong()));
        verify(bookingRepository, never())
                .save(any(Booking.class));
    }

    @Test
    void create_whenUserNotFound_assertThrowsUserNotFoundException() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(bookingService, "userRepository", userRepository);
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        ReflectionTestUtils.setField(bookingService, "itemRepository", itemRepository);
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        BookingDtoIn dtoIn = generator.nextObject(BookingDtoIn.class);
        dtoIn.setEnd(LocalDateTime.MAX);
        when(itemRepository.existsByIdAndAvailableIsFalse(anyLong()))
                .thenReturn(false);
        when(itemRepository.existsByIdAndOwnerId(anyLong(), anyLong()))
                .thenReturn(false);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class,
                () -> bookingService.create(dtoIn, generator.nextLong()));
        verify(bookingRepository, never())
                .save(any(Booking.class));
    }

    @Test
    void create_whenItemNotFound_assertThrowsEntityNotFoundException() {
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        ReflectionTestUtils.setField(bookingService, "itemRepository", itemRepository);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(bookingService, "userRepository", userRepository);
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        BookingDtoIn dtoIn = generator.nextObject(BookingDtoIn.class);
        dtoIn.setEnd(LocalDateTime.MAX);
        User user = generator.nextObject(User.class);
        when(itemRepository.existsByIdAndAvailableIsFalse(anyLong()))
                .thenReturn(false);
        when(itemRepository.existsByIdAndOwnerId(anyLong(), anyLong()))
                .thenReturn(false);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> bookingService.create(dtoIn, user.getId()));
        verify(bookingRepository, never())
                .save(any(Booking.class));
    }

    @Test
    void create() {
        ReflectionTestUtils.setField(bookingService, "bookingMapper", new BookingMapper());
        ReflectionTestUtils.setField(bookingService, "itemMapper", new ItemMapper());
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        ReflectionTestUtils.setField(bookingService, "itemRepository", itemRepository);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(bookingService, "userRepository", userRepository);
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        BookingDtoIn dtoIn = generator.nextObject(BookingDtoIn.class);
        dtoIn.setEnd(LocalDateTime.MAX);
        User user = generator.nextObject(User.class);
        Item item = generator.nextObject(Item.class);
        when(itemRepository.existsByIdAndAvailableIsFalse(anyLong()))
                .thenReturn(false);
        when(itemRepository.existsByIdAndOwnerId(anyLong(), anyLong()))
                .thenReturn(false);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(bookingRepository.save(any(Booking.class)))
                .then(returnsFirstArg());
        BookingDtoOut dtoOut = bookingService.create(dtoIn, user.getId());
        assertEquals(dtoIn.getId(), dtoOut.getId());
        assertEquals(dtoIn.getStart(), dtoOut.getStart());
        assertEquals(dtoIn.getEnd(), dtoOut.getEnd());
        assertEquals(item.getId(), dtoOut.getItem().getId());
        assertEquals(item.getName(), dtoOut.getItem().getName());
        assertEquals(item.getDescription(), dtoOut.getItem().getDescription());
        assertEquals(item.getAvailable(), dtoOut.getItem().isAvailable());
        assertNotNull(dtoOut.getItem().getRequestId());
        assertEquals(item.getOwner().getId(), dtoOut.getItem().getOwnerId());
        assertEquals(user.getId(), dtoOut.getBooker().getId());
        assertEquals(Status.WAITING, dtoOut.getStatus());
        verify(bookingRepository, times(1))
                .save(any(Booking.class));
    }

    @Test
    void update_whenStartAfterEnd_assertThrowsRentTimeConstraintException() {
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        BookingDtoIn dtoIn = generator.nextObject(BookingDtoIn.class);
        dtoIn.setEnd(LocalDateTime.MIN);
        assertThrows(RentTimeConstraintException.class,
                () -> bookingService.create(dtoIn, generator.nextLong()));
        verify(bookingRepository, never())
                .save(any(Booking.class));
    }

    @Test
    void update_whenUserNotBooker_assertThrowsObjectOwnerException() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(bookingService, "userRepository", userRepository);
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        BookingDtoIn dtoIn = generator.nextObject(BookingDtoIn.class);
        dtoIn.setEnd(LocalDateTime.MAX);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.existsByIdAndBookerId(anyLong(), anyLong()))
                .thenReturn(false);
        assertThrows(ObjectOwnerException.class,
                () -> bookingService.update(dtoIn, generator.nextLong()));
        verify(bookingRepository, never())
                .save(any(Booking.class));
    }

    @Test
    void update_whenUserNotFound_assertThrowsUserNotFoundException() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(bookingService, "userRepository", userRepository);
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        BookingDtoIn dtoIn = generator.nextObject(BookingDtoIn.class);
        dtoIn.setEnd(LocalDateTime.MAX);
        assertThrows(UserNotFoundException.class,
                () -> bookingService.update(dtoIn, generator.nextLong()));
        verify(bookingRepository, never())
                .save(any(Booking.class));
    }

    @Test
    void update_whenItemNotFound_assertThrowsEntityNotFoundException() {
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        ReflectionTestUtils.setField(bookingService, "itemRepository", itemRepository);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(bookingService, "userRepository", userRepository);
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        User user = generator.nextObject(User.class);
        BookingDtoIn dtoIn = generator.nextObject(BookingDtoIn.class);
        dtoIn.setEnd(LocalDateTime.MAX);
        when(bookingRepository.existsByIdAndBookerId(anyLong(), anyLong()))
                .thenReturn(true);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> bookingService.update(dtoIn, generator.nextLong()));
        verify(bookingRepository, never())
                .save(any(Booking.class));
    }

    @Test
    void update() {
        ReflectionTestUtils.setField(bookingService, "bookingMapper", new BookingMapper());
        ReflectionTestUtils.setField(bookingService, "itemMapper", new ItemMapper());
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        ReflectionTestUtils.setField(bookingService, "itemRepository", itemRepository);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(bookingService, "userRepository", userRepository);
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        User user = generator.nextObject(User.class);
        BookingDtoIn dtoIn = generator.nextObject(BookingDtoIn.class);
        dtoIn.setEnd(LocalDateTime.MAX);
        Item item = generator.nextObject(Item.class);
        when(bookingRepository.existsByIdAndBookerId(anyLong(), anyLong()))
                .thenReturn(true);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class)))
                .then(returnsFirstArg());
        BookingDtoOut dtoOut = bookingService.update(dtoIn, generator.nextLong());
        assertEquals(dtoIn.getId(), dtoOut.getId());
        assertEquals(dtoIn.getStart(), dtoOut.getStart());
        assertEquals(dtoIn.getEnd(), dtoOut.getEnd());
        assertNotNull(dtoOut.getItem());
        assertNotNull(dtoOut.getBooker());
        assertEquals(Status.WAITING, dtoOut.getStatus());
        verify(bookingRepository, times(1))
                .save(any(Booking.class));
    }

    @Test
    void findById_whenUserNotItemOwnerOrBooker_assertThrowsObjectOwnerException() {
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        when(bookingRepository.existsByBookingIdAndBookerOrItemOwnerId(anyLong(), anyLong()))
                .thenReturn(false);
        assertThrows(ObjectOwnerException.class,
                () -> bookingService.findById(generator.nextLong(), generator.nextLong()));
        verify(bookingRepository, never())
                .findByIdWithBookerAndItem(anyLong());
    }

    @Test
    void findById() {
        ReflectionTestUtils.setField(bookingService, "bookingMapper", new BookingMapper());
        ReflectionTestUtils.setField(bookingService, "itemMapper", new ItemMapper());
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        Booking booking = generator.nextObject(Booking.class);
        when(bookingRepository.existsByBookingIdAndBookerOrItemOwnerId(anyLong(), anyLong()))
                .thenReturn(true);
        when(bookingRepository.findByIdWithBookerAndItem(anyLong()))
                .thenReturn(Optional.ofNullable(booking));
        BookingDtoOut dtoOut = bookingService.findById(generator.nextLong(), generator.nextLong());
        assertNotNull(dtoOut.getId());
        assertNotNull(dtoOut.getStart());
        assertNotNull(dtoOut.getEnd());
        assertNotNull(dtoOut.getItem());
        assertNotNull(dtoOut.getBooker());
        assertNotNull(dtoOut.getStatus());
        verify(bookingRepository, times(1))
                .findByIdWithBookerAndItem(anyLong());
    }

    @Test
    void patch_whenUserNotItemOwner_assertThrowsObjectOwnerException() {
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        when(bookingRepository.existsByIdAndItemOwnerId(anyLong(), anyLong()))
                .thenReturn(false);
        assertThrows(ObjectOwnerException.class,
                () -> bookingService.patch(generator.nextLong(), generator.nextLong(), generator.nextBoolean()));
        verify(bookingRepository, never())
                .save(any(Booking.class));
    }

    @Test
    void patch_whenBookingStatusIsNotWaiting_assertThrowsBookingAlreadyApprovedException() {
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        when(bookingRepository.existsByIdAndItemOwnerId(anyLong(), anyLong()))
                .thenReturn(true);
        when(bookingRepository.existsByIdAndStatus(anyLong(), any(Status.class)))
                .thenReturn(false);
        assertThrows(BookingAlreadyApprovedException.class,
                () -> bookingService.patch(generator.nextLong(), generator.nextLong(), generator.nextBoolean()));
        verify(bookingRepository, never())
                .save(any(Booking.class));
    }

    @Test
    void patch_whenBookingIdIncorrect_assertThrowsObjectOwnerException() {
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        assertThrows(ObjectOwnerException.class,
                () -> bookingService.patch(generator.nextLong(), generator.nextLong(), generator.nextBoolean()));
        verify(bookingRepository, never())
                .save(any(Booking.class));
    }

    @Test
    void patch_returnDtoWithStatusApproved() {
        ReflectionTestUtils.setField(bookingService, "bookingMapper", new BookingMapper());
        ReflectionTestUtils.setField(bookingService, "itemMapper", new ItemMapper());
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        Booking booking = generator.nextObject(Booking.class);
        when(bookingRepository.existsByIdAndItemOwnerId(anyLong(), anyLong()))
                .thenReturn(true);
        when(bookingRepository.existsByIdAndStatus(anyLong(), eq(Status.WAITING)))
                .thenReturn(true);
        when(bookingRepository.findByIdWithBookerAndItem(anyLong()))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class)))
                .then(returnsFirstArg());
        BookingDtoOut dtoOut = bookingService.patch(booking.getId(), generator.nextLong(), true);
        assertEquals(booking.getId(), dtoOut.getId());
        assertEquals(booking.getStart(), dtoOut.getStart());
        assertEquals(booking.getEnd(), dtoOut.getEnd());
        assertEquals(booking.getItem().getId(), dtoOut.getItem().getId());
        assertEquals(booking.getItem().getName(), dtoOut.getItem().getName());
        assertEquals(booking.getItem().getDescription(), dtoOut.getItem().getDescription());
        assertEquals(booking.getItem().getAvailable(), dtoOut.getItem().isAvailable());
        assertEquals(booking.getItem().getRequest().getId(), dtoOut.getItem().getRequestId());
        assertEquals(booking.getBooker().getId(), dtoOut.getItem().getOwnerId());
        assertEquals(booking.getBooker().getId(), dtoOut.getBooker().getId());
        assertEquals(Status.APPROVED, dtoOut.getStatus());
        verify(bookingRepository, times(1))
                .save(any(Booking.class));
    }

    @Test
    void patch_returnDtoWithStatusRejected() {
        ReflectionTestUtils.setField(bookingService, "bookingMapper", new BookingMapper());
        ReflectionTestUtils.setField(bookingService, "itemMapper", new ItemMapper());
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        Booking booking = generator.nextObject(Booking.class);
        when(bookingRepository.existsByIdAndItemOwnerId(anyLong(), anyLong()))
                .thenReturn(true);
        when(bookingRepository.existsByIdAndStatus(anyLong(), eq(Status.WAITING)))
                .thenReturn(true);
        when(bookingRepository.findByIdWithBookerAndItem(anyLong()))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class)))
                .then(returnsFirstArg());
        BookingDtoOut dtoOut = bookingService.patch(booking.getId(), booking.getItem().getOwner().getId(), false);
        assertEquals(booking.getId(), dtoOut.getId());
        assertEquals(booking.getStart(), dtoOut.getStart());
        assertEquals(booking.getEnd(), dtoOut.getEnd());
        assertEquals(booking.getItem().getId(), dtoOut.getItem().getId());
        assertEquals(booking.getItem().getName(), dtoOut.getItem().getName());
        assertEquals(booking.getItem().getDescription(), dtoOut.getItem().getDescription());
        assertEquals(booking.getItem().getAvailable(), dtoOut.getItem().isAvailable());
        assertEquals(booking.getItem().getRequest().getId(), dtoOut.getItem().getRequestId());
        assertEquals(booking.getBooker().getId(), dtoOut.getItem().getOwnerId());
        assertEquals(booking.getBooker().getId(), dtoOut.getBooker().getId());
        assertEquals(Status.REJECTED, dtoOut.getStatus());
        verify(bookingRepository, times(1))
                .save(any(Booking.class));
    }

    @Test
    void findAllByItemOwnerIdAndState_whenUserNotFound_assertThrowsUserNotFoundException() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(bookingService, "userRepository", userRepository);
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);
        assertThrows(UserNotFoundException.class,
                () -> bookingService.findAllByItemOwnerId(from, limit, generator.nextLong(), State.ALL));
    }

    @Test
    void findAllByItemOwnerIdAndState_whenStateAll() {
        ReflectionTestUtils.setField(bookingService, "bookingMapper", new BookingMapper());
        ReflectionTestUtils.setField(bookingService, "itemMapper", new ItemMapper());
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(bookingService, "userRepository", userRepository);
        SearchByStateFactory searchByStateFactory = Mockito.mock(SearchByStateFactory.class, CALLS_REAL_METHODS);
        ReflectionTestUtils.setField(searchByStateFactory, "stateMap", Map.of("all", new All(bookingRepository)));
        ReflectionTestUtils.setField(bookingService, "searchByStateFactory", searchByStateFactory);
        Booking booking = generator.nextObject(Booking.class);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findAllByItemOwnerId(anyLong(), any(PageRequester.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByItemOwnerId(from, limit, generator.nextLong(), State.ALL);
        assertThat(bookingDtoOuts).hasSize(1);
        BookingDtoOut out = bookingDtoOuts.get(0);
        assertEquals(booking.getId(), out.getId());
        assertEquals(booking.getStart(), out.getStart());
        assertEquals(booking.getEnd(), out.getEnd());
        assertEquals(booking.getItem().getId(), out.getItem().getId());
        assertEquals(booking.getItem().getName(), out.getItem().getName());
        assertEquals(booking.getItem().getDescription(), out.getItem().getDescription());
        assertEquals(booking.getItem().getAvailable(), out.getItem().isAvailable());
        assertEquals(booking.getItem().getRequest().getId(), out.getItem().getRequestId());
        assertEquals(booking.getBooker().getId(), out.getItem().getOwnerId());
        assertEquals(booking.getBooker().getId(), out.getBooker().getId());
    }

    @Test
    void findAllByItemOwnerIdAndState_whenStateFuture() {
        ReflectionTestUtils.setField(bookingService, "bookingMapper", new BookingMapper());
        ReflectionTestUtils.setField(bookingService, "itemMapper", new ItemMapper());
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(bookingService, "userRepository", userRepository);
        SearchByStateFactory searchByStateFactory = Mockito.mock(SearchByStateFactory.class, CALLS_REAL_METHODS);
        ReflectionTestUtils.setField(searchByStateFactory, "stateMap", Map.of("future", new Future(bookingRepository)));
        ReflectionTestUtils.setField(bookingService, "searchByStateFactory", searchByStateFactory);
        Booking booking = generator.nextObject(Booking.class);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findAllByItemOwnerIdWhereStartInFuture(anyLong(), any(PageRequester.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByItemOwnerId(from, limit, generator.nextLong(), State.FUTURE);
        assertThat(bookingDtoOuts).hasSize(1);
        BookingDtoOut out = bookingDtoOuts.get(0);
        assertEquals(booking.getId(), out.getId());
        assertEquals(booking.getStart(), out.getStart());
        assertEquals(booking.getEnd(), out.getEnd());
        assertEquals(booking.getItem().getId(), out.getItem().getId());
        assertEquals(booking.getItem().getName(), out.getItem().getName());
        assertEquals(booking.getItem().getDescription(), out.getItem().getDescription());
        assertEquals(booking.getItem().getAvailable(), out.getItem().isAvailable());
        assertEquals(booking.getItem().getRequest().getId(), out.getItem().getRequestId());
        assertEquals(booking.getBooker().getId(), out.getItem().getOwnerId());
        assertEquals(booking.getBooker().getId(), out.getBooker().getId());
    }

    @Test
    void findAllByItemOwnerIdAndState_whenStateCurrent() {
        ReflectionTestUtils.setField(bookingService, "bookingMapper", new BookingMapper());
        ReflectionTestUtils.setField(bookingService, "itemMapper", new ItemMapper());
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(bookingService, "userRepository", userRepository);
        SearchByStateFactory searchByStateFactory = Mockito.mock(SearchByStateFactory.class, CALLS_REAL_METHODS);
        ReflectionTestUtils.setField(searchByStateFactory, "stateMap", Map.of("current", new Current(bookingRepository)));
        ReflectionTestUtils.setField(bookingService, "searchByStateFactory", searchByStateFactory);
        Booking booking = generator.nextObject(Booking.class);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findAllByItemOwnerIdWithTimestampsBetweenStartAndEnd(anyLong(), any(PageRequester.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByItemOwnerId(from, limit, generator.nextLong(), State.CURRENT);
        assertThat(bookingDtoOuts).hasSize(1);
        BookingDtoOut out = bookingDtoOuts.get(0);
        assertEquals(booking.getId(), out.getId());
        assertEquals(booking.getStart(), out.getStart());
        assertEquals(booking.getEnd(), out.getEnd());
        assertEquals(booking.getItem().getId(), out.getItem().getId());
        assertEquals(booking.getItem().getName(), out.getItem().getName());
        assertEquals(booking.getItem().getDescription(), out.getItem().getDescription());
        assertEquals(booking.getItem().getAvailable(), out.getItem().isAvailable());
        assertEquals(booking.getItem().getRequest().getId(), out.getItem().getRequestId());
        assertEquals(booking.getBooker().getId(), out.getItem().getOwnerId());
        assertEquals(booking.getBooker().getId(), out.getBooker().getId());
    }

    @Test
    void findAllByItemOwnerIdAndState_whenStatePast() {
        ReflectionTestUtils.setField(bookingService, "bookingMapper", new BookingMapper());
        ReflectionTestUtils.setField(bookingService, "itemMapper", new ItemMapper());
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(bookingService, "userRepository", userRepository);
        SearchByStateFactory searchByStateFactory = Mockito.mock(SearchByStateFactory.class, CALLS_REAL_METHODS);
        ReflectionTestUtils.setField(searchByStateFactory, "stateMap", Map.of("past", new Past(bookingRepository)));
        ReflectionTestUtils.setField(bookingService, "searchByStateFactory", searchByStateFactory);
        Booking booking = generator.nextObject(Booking.class);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findAllByItemOwnerIdWhereEndInPast(anyLong(), any(PageRequester.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByItemOwnerId(from, limit, generator.nextLong(), State.PAST);
        assertThat(bookingDtoOuts).hasSize(1);
        BookingDtoOut out = bookingDtoOuts.get(0);
        assertEquals(booking.getId(), out.getId());
        assertEquals(booking.getStart(), out.getStart());
        assertEquals(booking.getEnd(), out.getEnd());
        assertEquals(booking.getItem().getId(), out.getItem().getId());
        assertEquals(booking.getItem().getName(), out.getItem().getName());
        assertEquals(booking.getItem().getDescription(), out.getItem().getDescription());
        assertEquals(booking.getItem().getAvailable(), out.getItem().isAvailable());
        assertEquals(booking.getItem().getRequest().getId(), out.getItem().getRequestId());
        assertEquals(booking.getBooker().getId(), out.getItem().getOwnerId());
        assertEquals(booking.getBooker().getId(), out.getBooker().getId());
    }

    @Test
    void findAllByItemOwnerIdAndState_whenStateRejected() {
        ReflectionTestUtils.setField(bookingService, "bookingMapper", new BookingMapper());
        ReflectionTestUtils.setField(bookingService, "itemMapper", new ItemMapper());
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(bookingService, "userRepository", userRepository);
        SearchByStateFactory searchByStateFactory = Mockito.mock(SearchByStateFactory.class, CALLS_REAL_METHODS);
        ReflectionTestUtils.setField(searchByStateFactory, "stateMap", Map.of("rejected", new Rejected(bookingRepository)));
        ReflectionTestUtils.setField(bookingService, "searchByStateFactory", searchByStateFactory);
        Booking booking = generator.nextObject(Booking.class);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findBookingsByItemOwnerIdAndStatus(anyLong(), eq(Status.REJECTED), any(PageRequester.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByItemOwnerId(from, limit, generator.nextLong(), State.REJECTED);
        assertThat(bookingDtoOuts).hasSize(1);
        BookingDtoOut out = bookingDtoOuts.get(0);
        assertEquals(booking.getId(), out.getId());
        assertEquals(booking.getStart(), out.getStart());
        assertEquals(booking.getEnd(), out.getEnd());
        assertEquals(booking.getItem().getId(), out.getItem().getId());
        assertEquals(booking.getItem().getName(), out.getItem().getName());
        assertEquals(booking.getItem().getDescription(), out.getItem().getDescription());
        assertEquals(booking.getItem().getAvailable(), out.getItem().isAvailable());
        assertEquals(booking.getItem().getRequest().getId(), out.getItem().getRequestId());
        assertEquals(booking.getBooker().getId(), out.getItem().getOwnerId());
        assertEquals(booking.getBooker().getId(), out.getBooker().getId());
    }

    @Test
    void findAllByItemOwnerIdAndState_whenStateWaiting() {
        ReflectionTestUtils.setField(bookingService, "bookingMapper", new BookingMapper());
        ReflectionTestUtils.setField(bookingService, "itemMapper", new ItemMapper());
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(bookingService, "userRepository", userRepository);
        SearchByStateFactory searchByStateFactory = Mockito.mock(SearchByStateFactory.class, CALLS_REAL_METHODS);
        ReflectionTestUtils.setField(searchByStateFactory, "stateMap", Map.of("waiting", new Waiting(bookingRepository)));
        ReflectionTestUtils.setField(bookingService, "searchByStateFactory", searchByStateFactory);
        Booking booking = generator.nextObject(Booking.class);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findBookingsByItemOwnerIdAndStatus(anyLong(), eq(Status.WAITING), any(PageRequester.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByItemOwnerId(from, limit, generator.nextLong(), State.WAITING);
        assertThat(bookingDtoOuts).hasSize(1);
        BookingDtoOut out = bookingDtoOuts.get(0);
        assertEquals(booking.getId(), out.getId());
        assertEquals(booking.getStart(), out.getStart());
        assertEquals(booking.getEnd(), out.getEnd());
        assertEquals(booking.getItem().getId(), out.getItem().getId());
        assertEquals(booking.getItem().getName(), out.getItem().getName());
        assertEquals(booking.getItem().getDescription(), out.getItem().getDescription());
        assertEquals(booking.getItem().getAvailable(), out.getItem().isAvailable());
        assertEquals(booking.getItem().getRequest().getId(), out.getItem().getRequestId());
        assertEquals(booking.getBooker().getId(), out.getItem().getOwnerId());
        assertEquals(booking.getBooker().getId(), out.getBooker().getId());
    }

    @Test
    void findAllByOwnerIdAndState_whenStateUnknown_assertThrowsUnknownStateException() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(bookingService, "userRepository", userRepository);
        SearchByStateFactory searchByStateFactory = Mockito.mock(SearchByStateFactory.class, CALLS_REAL_METHODS);
        ReflectionTestUtils.setField(searchByStateFactory, "stateMap", Map.of());
        ReflectionTestUtils.setField(bookingService, "searchByStateFactory", searchByStateFactory);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        assertThrows(UnknownStateException.class,
                () -> bookingService.findAllByItemOwnerId(from, limit, generator.nextLong(), State.UNKNOWN));
    }

    @Test
    void findAllByBookerIdAndState_whenUserNotFound_assertThrowsUserNotFoundException() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(bookingService, "userRepository", userRepository);
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);
        assertThrows(UserNotFoundException.class,
                () -> bookingService.findAllByBookerId(from, limit, generator.nextLong(), State.ALL));
    }

    @Test
    void findAllByBookerIdAndState_whenStateAll() {
        ReflectionTestUtils.setField(bookingService, "bookingMapper", new BookingMapper());
        ReflectionTestUtils.setField(bookingService, "itemMapper", new ItemMapper());
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(bookingService, "userRepository", userRepository);
        SearchByStateFactory searchByStateFactory = Mockito.mock(SearchByStateFactory.class, CALLS_REAL_METHODS);
        ReflectionTestUtils.setField(searchByStateFactory, "stateMap", Map.of("all", new All(bookingRepository)));
        ReflectionTestUtils.setField(bookingService, "searchByStateFactory", searchByStateFactory);
        Booking booking = generator.nextObject(Booking.class);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findAllByBookerId(anyLong(), any(PageRequester.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByBookerId(from, limit, generator.nextLong(), State.ALL);
        assertThat(bookingDtoOuts).hasSize(1);
        BookingDtoOut out = bookingDtoOuts.get(0);
        assertEquals(booking.getId(), out.getId());
        assertEquals(booking.getStart(), out.getStart());
        assertEquals(booking.getEnd(), out.getEnd());
        assertEquals(booking.getItem().getId(), out.getItem().getId());
        assertEquals(booking.getItem().getName(), out.getItem().getName());
        assertEquals(booking.getItem().getDescription(), out.getItem().getDescription());
        assertEquals(booking.getItem().getAvailable(), out.getItem().isAvailable());
        assertEquals(booking.getItem().getRequest().getId(), out.getItem().getRequestId());
        assertEquals(booking.getBooker().getId(), out.getItem().getOwnerId());
        assertEquals(booking.getBooker().getId(), out.getBooker().getId());
    }

    @Test
    void findAllByBookerIdAndState_whenStateCurrent() {
        ReflectionTestUtils.setField(bookingService, "bookingMapper", new BookingMapper());
        ReflectionTestUtils.setField(bookingService, "itemMapper", new ItemMapper());
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(bookingService, "userRepository", userRepository);
        SearchByStateFactory searchByStateFactory = Mockito.mock(SearchByStateFactory.class, CALLS_REAL_METHODS);
        ReflectionTestUtils.setField(searchByStateFactory, "stateMap", Map.of("current", new Current(bookingRepository)));
        ReflectionTestUtils.setField(bookingService, "searchByStateFactory", searchByStateFactory);
        Booking booking = generator.nextObject(Booking.class);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findAllByBookerIdWithTimestampsBetweenStartAndEnd(anyLong(), any(PageRequester.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByBookerId(from, limit, generator.nextLong(), State.CURRENT);
        assertThat(bookingDtoOuts).hasSize(1);
        BookingDtoOut out = bookingDtoOuts.get(0);
        assertEquals(booking.getId(), out.getId());
        assertEquals(booking.getStart(), out.getStart());
        assertEquals(booking.getEnd(), out.getEnd());
        assertEquals(booking.getItem().getId(), out.getItem().getId());
        assertEquals(booking.getItem().getName(), out.getItem().getName());
        assertEquals(booking.getItem().getDescription(), out.getItem().getDescription());
        assertEquals(booking.getItem().getAvailable(), out.getItem().isAvailable());
        assertEquals(booking.getItem().getRequest().getId(), out.getItem().getRequestId());
        assertEquals(booking.getBooker().getId(), out.getItem().getOwnerId());
        assertEquals(booking.getBooker().getId(), out.getBooker().getId());
    }

    @Test
    void findAllByBookerIdAndState_whenStateFuture() {
        ReflectionTestUtils.setField(bookingService, "bookingMapper", new BookingMapper());
        ReflectionTestUtils.setField(bookingService, "itemMapper", new ItemMapper());
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(bookingService, "userRepository", userRepository);
        SearchByStateFactory searchByStateFactory = Mockito.mock(SearchByStateFactory.class, CALLS_REAL_METHODS);
        ReflectionTestUtils.setField(searchByStateFactory, "stateMap", Map.of("future", new Future(bookingRepository)));
        ReflectionTestUtils.setField(bookingService, "searchByStateFactory", searchByStateFactory);
        Booking booking = generator.nextObject(Booking.class);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findAllByBookerIdWhereStartInFuture(anyLong(), any(PageRequester.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByBookerId(from, limit, generator.nextLong(), State.FUTURE);
        assertThat(bookingDtoOuts).hasSize(1);
        BookingDtoOut out = bookingDtoOuts.get(0);
        assertEquals(booking.getId(), out.getId());
        assertEquals(booking.getStart(), out.getStart());
        assertEquals(booking.getEnd(), out.getEnd());
        assertEquals(booking.getItem().getId(), out.getItem().getId());
        assertEquals(booking.getItem().getName(), out.getItem().getName());
        assertEquals(booking.getItem().getDescription(), out.getItem().getDescription());
        assertEquals(booking.getItem().getAvailable(), out.getItem().isAvailable());
        assertEquals(booking.getItem().getRequest().getId(), out.getItem().getRequestId());
        assertEquals(booking.getBooker().getId(), out.getItem().getOwnerId());
        assertEquals(booking.getBooker().getId(), out.getBooker().getId());
    }

    @Test
    void findAllByBookerIdAndState_whenStatePast() {
        ReflectionTestUtils.setField(bookingService, "bookingMapper", new BookingMapper());
        ReflectionTestUtils.setField(bookingService, "itemMapper", new ItemMapper());
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(bookingService, "userRepository", userRepository);
        SearchByStateFactory searchByStateFactory = Mockito.mock(SearchByStateFactory.class, CALLS_REAL_METHODS);
        ReflectionTestUtils.setField(searchByStateFactory, "stateMap", Map.of("past", new Past(bookingRepository)));
        ReflectionTestUtils.setField(bookingService, "searchByStateFactory", searchByStateFactory);
        Booking booking = generator.nextObject(Booking.class);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findAllByBookerIdWhereEndInPast(anyLong(), any(PageRequester.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByBookerId(from, limit, generator.nextLong(), State.PAST);
        assertThat(bookingDtoOuts).hasSize(1);
        BookingDtoOut out = bookingDtoOuts.get(0);
        assertEquals(booking.getId(), out.getId());
        assertEquals(booking.getStart(), out.getStart());
        assertEquals(booking.getEnd(), out.getEnd());
        assertEquals(booking.getItem().getId(), out.getItem().getId());
        assertEquals(booking.getItem().getName(), out.getItem().getName());
        assertEquals(booking.getItem().getDescription(), out.getItem().getDescription());
        assertEquals(booking.getItem().getAvailable(), out.getItem().isAvailable());
        assertEquals(booking.getItem().getRequest().getId(), out.getItem().getRequestId());
        assertEquals(booking.getBooker().getId(), out.getItem().getOwnerId());
        assertEquals(booking.getBooker().getId(), out.getBooker().getId());
    }

    @Test
    void findAllByBookerIdAndState_whenStateRejected() {
        ReflectionTestUtils.setField(bookingService, "bookingMapper", new BookingMapper());
        ReflectionTestUtils.setField(bookingService, "itemMapper", new ItemMapper());
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(bookingService, "userRepository", userRepository);
        SearchByStateFactory searchByStateFactory = Mockito.mock(SearchByStateFactory.class, CALLS_REAL_METHODS);
        ReflectionTestUtils.setField(searchByStateFactory, "stateMap", Map.of("rejected", new Rejected(bookingRepository)));
        ReflectionTestUtils.setField(bookingService, "searchByStateFactory", searchByStateFactory);
        Booking booking = generator.nextObject(Booking.class);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findBookingsByBookerIdAndStatus(anyLong(), eq(Status.REJECTED), any(PageRequester.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByBookerId(from, limit, generator.nextLong(), State.REJECTED);
        assertThat(bookingDtoOuts).hasSize(1);
        BookingDtoOut out = bookingDtoOuts.get(0);
        assertEquals(booking.getId(), out.getId());
        assertEquals(booking.getStart(), out.getStart());
        assertEquals(booking.getEnd(), out.getEnd());
        assertEquals(booking.getItem().getId(), out.getItem().getId());
        assertEquals(booking.getItem().getName(), out.getItem().getName());
        assertEquals(booking.getItem().getDescription(), out.getItem().getDescription());
        assertEquals(booking.getItem().getAvailable(), out.getItem().isAvailable());
        assertEquals(booking.getItem().getRequest().getId(), out.getItem().getRequestId());
        assertEquals(booking.getBooker().getId(), out.getItem().getOwnerId());
        assertEquals(booking.getBooker().getId(), out.getBooker().getId());
    }

    @Test
    void findAllByBookerIdAndState_whenStateWaiting() {
        ReflectionTestUtils.setField(bookingService, "bookingMapper", new BookingMapper());
        ReflectionTestUtils.setField(bookingService, "itemMapper", new ItemMapper());
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(bookingService, "userRepository", userRepository);
        SearchByStateFactory searchByStateFactory = Mockito.mock(SearchByStateFactory.class, CALLS_REAL_METHODS);
        ReflectionTestUtils.setField(searchByStateFactory, "stateMap", Map.of("waiting", new Waiting(bookingRepository)));
        ReflectionTestUtils.setField(bookingService, "searchByStateFactory", searchByStateFactory);
        Booking booking = generator.nextObject(Booking.class);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findBookingsByBookerIdAndStatus(anyLong(), eq(Status.WAITING), any(PageRequester.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByBookerId(from, limit, generator.nextLong(), State.WAITING);
        assertThat(bookingDtoOuts).hasSize(1);
        BookingDtoOut out = bookingDtoOuts.get(0);
        assertEquals(booking.getId(), out.getId());
        assertEquals(booking.getStart(), out.getStart());
        assertEquals(booking.getEnd(), out.getEnd());
        assertEquals(booking.getItem().getId(), out.getItem().getId());
        assertEquals(booking.getItem().getName(), out.getItem().getName());
        assertEquals(booking.getItem().getDescription(), out.getItem().getDescription());
        assertEquals(booking.getItem().getAvailable(), out.getItem().isAvailable());
        assertEquals(booking.getItem().getRequest().getId(), out.getItem().getRequestId());
        assertEquals(booking.getBooker().getId(), out.getItem().getOwnerId());
        assertEquals(booking.getBooker().getId(), out.getBooker().getId());
    }

    @Test
    void findAllByBookerIdAndState_whenStateUnknown_assertThrowsUnknownStateException() {
        ReflectionTestUtils.setField(bookingService, "bookingMapper", new BookingMapper());
        ReflectionTestUtils.setField(bookingService, "itemMapper", new ItemMapper());
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(bookingService, "userRepository", userRepository);
        SearchByStateFactory searchByStateFactory = Mockito.mock(SearchByStateFactory.class, CALLS_REAL_METHODS);
        ReflectionTestUtils.setField(searchByStateFactory, "stateMap", Map.of());
        ReflectionTestUtils.setField(bookingService, "searchByStateFactory", searchByStateFactory);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        assertThrows(UnknownStateException.class,
                () -> bookingService.findAllByBookerId(from, limit, generator.nextLong(), State.UNKNOWN));
    }
}
