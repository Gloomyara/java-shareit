package ru.practicum.shareit.item;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.item.UnregisteredBookingException;
import ru.practicum.shareit.exceptions.user.ObjectOwnerException;
import ru.practicum.shareit.exceptions.user.UserNotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.config.ObjectMapperConfig;
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
class ItemServiceTest {

    private ItemServiceImpl itemService;
    private final EasyRandom generator = new EasyRandom();
    private final ProjectionFactory factory = new SpelAwareProxyProjectionFactory();
    private final BookingShort next = factory.createProjection(BookingShort.class);
    private final BookingShort last = factory.createProjection(BookingShort.class);

    private final int from = Integer.parseInt(DEFAULT_FROM);
    private final int limit = Integer.parseInt(DEFAULT_LIMIT);

    @BeforeEach
    void setUp() {
        itemService = Mockito.mock(ItemServiceImpl.class, CALLS_REAL_METHODS);
    }

    @Test
    void create_whenUserNotExist_assertThrowsUserNotFoundException() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(itemService, "userRepository", userRepository);
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        ReflectionTestUtils.setField(itemService, "repository", itemRepository);
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);
        assertThrows(UserNotFoundException.class,
                () -> itemService.save(generator.nextObject(ItemDtoIn.class), generator.nextLong()));
        verify(itemRepository, never())
                .save(any(Item.class));
    }

    @Test
    void create_whenRequestIdNull() {

        ReflectionTestUtils.setField(itemService, "itemMapper", new ItemMapper());
        ReflectionTestUtils.setField(itemService, "commentMapper", new CommentMapper());
        ReflectionTestUtils.setField(itemService, "bookingMapper", new BookingMapper());
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(itemService, "userRepository", userRepository);
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        ReflectionTestUtils.setField(itemService, "repository", itemRepository);
        User owner = generator.nextObject(User.class);
        ItemDtoIn dtoIn = generator.nextObject(ItemDtoIn.class);
        dtoIn.setRequestId(null);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class)))
                .then(returnsFirstArg());
        ItemDtoOut created = itemService.save(dtoIn, owner.getId());
        assertNotNull(created.getId());
        assertEquals(dtoIn.getName(), created.getName());
        assertEquals(dtoIn.getDescription(), created.getDescription());
        assertEquals(dtoIn.getAvailable(), created.getAvailable());
        assertEquals(dtoIn.getRequestId(), created.getRequestId());
        assertNull(created.getLastBooking());
        assertNull(created.getNextBooking());
        assertNull(created.getComments());
        verify(itemRepository, times(1))
                .save(any(Item.class));
    }

    @Test
    void create_whenRequestIdNotNull() {
        ReflectionTestUtils.setField(itemService, "itemMapper", new ItemMapper());
        ReflectionTestUtils.setField(itemService, "commentMapper", new CommentMapper());
        ReflectionTestUtils.setField(itemService, "bookingMapper", new BookingMapper());
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(itemService, "userRepository", userRepository);
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        ReflectionTestUtils.setField(itemService, "repository", itemRepository);
        RequestRepository requestRepository = Mockito.mock(RequestRepository.class);
        ReflectionTestUtils.setField(itemService, "requestRepository", requestRepository);
        User owner = generator.nextObject(User.class);
        ItemDtoIn dtoIn = generator.nextObject(ItemDtoIn.class);
        Request request = generator.nextObject(Request.class);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(requestRepository.findByIdWithItems(anyLong()))
                .thenReturn(Optional.of(request));
        when(itemRepository.save(any(Item.class)))
                .then(returnsFirstArg());
        ItemDtoOut created = itemService.save(dtoIn, owner.getId());
        assertNotNull(created.getId());
        assertEquals(dtoIn.getName(), created.getName());
        assertEquals(dtoIn.getDescription(), created.getDescription());
        assertEquals(dtoIn.getAvailable(), created.getAvailable());
        assertEquals(request.getId(), created.getRequestId());
        assertNull(created.getLastBooking());
        assertNull(created.getNextBooking());
        assertNull(created.getComments());
        verify(itemRepository, times(1))
                .save(any(Item.class));
    }

    @Test
    void update_whenUserNotExist_assertThrowsObjectOwnerException() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(itemService, "userRepository", userRepository);
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        ReflectionTestUtils.setField(itemService, "repository", itemRepository);
        assertThrows(ObjectOwnerException.class,
                () -> itemService.update(generator.nextObject(ItemDtoIn.class), generator.nextLong()));
        verify(itemRepository, never())
                .save(any(Item.class));
    }

    @Test
    void update_whenUserNotItemOwner_assertThrowsObjectOwnerException() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(itemService, "userRepository", userRepository);
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        ReflectionTestUtils.setField(itemService, "repository", itemRepository);
        when(itemRepository.existsByIdAndOwnerId(anyLong(), anyLong()))
                .thenReturn(false);
        assertThrows(ObjectOwnerException.class,
                () -> itemService.update(generator.nextObject(ItemDtoIn.class), generator.nextLong()));
        verify(itemRepository, never())
                .save(any(Item.class));
    }

    @Test
    void update() {
        ReflectionTestUtils.setField(itemService, "itemMapper", new ItemMapper());
        ReflectionTestUtils.setField(itemService, "commentMapper", new CommentMapper());
        ReflectionTestUtils.setField(itemService, "bookingMapper", new BookingMapper());
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(itemService, "userRepository", userRepository);
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        ReflectionTestUtils.setField(itemService, "repository", itemRepository);
        RequestRepository requestRepository = Mockito.mock(RequestRepository.class);
        ReflectionTestUtils.setField(itemService, "requestRepository", requestRepository);
        User owner = generator.nextObject(User.class);
        ItemDtoIn dtoIn = generator.nextObject(ItemDtoIn.class);
        Request request = generator.nextObject(Request.class);
        dtoIn.setRequestId(request.getId());
        when(itemRepository.existsByIdAndOwnerId(dtoIn.getId(), owner.getId()))
                .thenReturn(true);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(requestRepository.findByIdWithItems(anyLong()))
                .thenReturn(Optional.of(request));
        when(itemRepository.save(any(Item.class)))
                .then(returnsFirstArg());
        ItemDtoOut updated = itemService.update(dtoIn, owner.getId());
        assertNotNull(updated.getId());
        assertEquals(dtoIn.getName(), updated.getName());
        assertEquals(dtoIn.getDescription(), updated.getDescription());
        assertEquals(dtoIn.getAvailable(), updated.getAvailable());
        assertEquals(dtoIn.getRequestId(), updated.getRequestId());
        assertNull(updated.getLastBooking());
        assertNull(updated.getNextBooking());
        assertNull(updated.getComments());
        verify(itemRepository, times(1))
                .save(any(Item.class));
    }

    @Test
    void patch_whenUserNotExist_assertThrowsObjectOwnerException() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(itemService, "userRepository", userRepository);
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        ReflectionTestUtils.setField(itemService, "repository", itemRepository);
        assertThrows(ObjectOwnerException.class,
                () -> itemService.patch(generator.nextLong(), Map.of(), generator.nextLong()));
        verify(itemRepository, never())
                .save(any(Item.class));
    }

    @Test
    void patch_whenUserNotItemOwner_assertThrowsObjectOwnerException() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(itemService, "userRepository", userRepository);
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        ReflectionTestUtils.setField(itemService, "repository", itemRepository);
        when(itemRepository.existsByIdAndOwnerId(anyLong(), anyLong()))
                .thenReturn(false);
        assertThrows(ObjectOwnerException.class,
                () -> itemService.patch(generator.nextLong(), Map.of(), generator.nextLong()));
        verify(itemRepository, never())
                .save(any(Item.class));
    }

    @Test
    void patch() {
        ReflectionTestUtils.setField(itemService, "itemMapper", new ItemMapper());
        ReflectionTestUtils.setField(itemService, "commentMapper", new CommentMapper());
        ReflectionTestUtils.setField(itemService, "bookingMapper", new BookingMapper());
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(itemService, "userRepository", userRepository);
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        ReflectionTestUtils.setField(itemService, "repository", itemRepository);
        ReflectionTestUtils.setField(itemService, "objectMapper", new ObjectMapperConfig().objectMapper());
        User owner = generator.nextObject(User.class);
        Item item = generator.nextObject(Item.class);
        item.setOwner(owner);
        when(itemRepository.existsByIdAndOwnerId(anyLong(), anyLong()))
                .thenReturn(true);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class)))
                .then(returnsFirstArg());
        ItemDtoOut updated = itemService.patch(item.getId(), Map.of(), owner.getId());
        assertNotNull(updated.getId());
        assertEquals(item.getName(), updated.getName());
        assertEquals(item.getDescription(), updated.getDescription());
        assertEquals(item.getAvailable(), updated.getAvailable());
        assertEquals(item.getRequest().getId(), updated.getRequestId());
        assertNull(updated.getLastBooking());
        assertNull(updated.getNextBooking());
        assertNotNull(updated.getComments());
        verify(itemRepository, times(1))
                .save(any(Item.class));
    }

    @Test
    void findById_whenUserNotExist_assertThrowsUserNotFoundException() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(itemService, "userRepository", userRepository);
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        ReflectionTestUtils.setField(itemService, "repository", itemRepository);
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);
        assertThrows(UserNotFoundException.class,
                () -> itemService.findById(generator.nextLong(), generator.nextLong()));
        verify(itemRepository, never())
                .save(any(Item.class));
    }

    @Test
    void findById_whenUserItemOwner() {
        ReflectionTestUtils.setField(itemService, "itemMapper", new ItemMapper());
        ReflectionTestUtils.setField(itemService, "commentMapper", new CommentMapper());
        ReflectionTestUtils.setField(itemService, "bookingMapper", new BookingMapper());
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(itemService, "userRepository", userRepository);
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        ReflectionTestUtils.setField(itemService, "repository", itemRepository);
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(itemService, "bookingRepository", bookingRepository);
        User owner = generator.nextObject(User.class);
        Item item = generator.nextObject(Item.class);
        item.setOwner(owner);
        next.setId(generator.nextLong());
        next.setItemId(item.getId());
        next.setBookerId(generator.nextLong());
        last.setId(generator.nextLong());
        last.setItemId(item.getId());
        last.setBookerId(generator.nextLong());
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRepository.findByIdWithOwnerAndComments(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findTopByItemIdAndStatusAndStartBeforeOrderByStartDesc(
                anyLong(), any(Status.class), any(LocalDateTime.class)))
                .thenReturn(Optional.of(last));
        when(bookingRepository.findTopByItemIdAndStatusAndStartAfterOrderByStart(
                anyLong(), any(Status.class), any(LocalDateTime.class))).thenReturn(Optional.of(next));
        ItemDtoOut found = itemService.findById(item.getId(), owner.getId());
        assertEquals(item.getId(), found.getId());
        assertEquals(item.getName(), found.getName());
        assertEquals(item.getDescription(), found.getDescription());
        assertEquals(item.getAvailable(), found.getAvailable());
        assertEquals(item.getRequest().getId(), found.getRequestId());
        assertEquals(last.getId(), found.getLastBooking().getId());
        assertEquals(last.getBookerId(), found.getLastBooking().getBookerId());
        assertEquals(next.getId(), found.getNextBooking().getId());
        assertEquals(next.getBookerId(), found.getNextBooking().getBookerId());
        verify(itemRepository, times(1))
                .findByIdWithOwnerAndComments(anyLong());
        verify(bookingRepository, times(1))
                .findTopByItemIdAndStatusAndStartBeforeOrderByStartDesc(
                        anyLong(), any(Status.class), any(LocalDateTime.class));
        verify(bookingRepository, times(1))
                .findTopByItemIdAndStatusAndStartAfterOrderByStart(
                        anyLong(), any(Status.class), any(LocalDateTime.class));
    }

    @Test
    void findById_whenUserNotItemOwner() {
        ReflectionTestUtils.setField(itemService, "itemMapper", new ItemMapper());
        ReflectionTestUtils.setField(itemService, "commentMapper", new CommentMapper());
        ReflectionTestUtils.setField(itemService, "bookingMapper", new BookingMapper());
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(itemService, "userRepository", userRepository);
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        ReflectionTestUtils.setField(itemService, "repository", itemRepository);
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(itemService, "bookingRepository", bookingRepository);
        User owner = generator.nextObject(User.class);
        Item item = generator.nextObject(Item.class);
        item.setOwner(owner);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRepository.findByIdWithOwnerAndComments(anyLong()))
                .thenReturn(Optional.of(item));
        ItemDtoOut found = itemService.findById(item.getId(), generator.nextLong());
        assertEquals(item.getId(), found.getId());
        assertEquals(item.getName(), found.getName());
        assertEquals(item.getDescription(), found.getDescription());
        assertEquals(item.getAvailable(), found.getAvailable());
        assertEquals(item.getRequest().getId(), found.getRequestId());
        assertNull(found.getLastBooking());
        assertNull(found.getNextBooking());
        verify(itemRepository, times(1))
                .findByIdWithOwnerAndComments(anyLong());
        verify(bookingRepository, never())
                .findTopByItemIdAndStatusAndStartBeforeOrderByStartDesc(
                        anyLong(), any(Status.class), any(LocalDateTime.class));
        verify(bookingRepository, never())
                .findTopByItemIdAndStatusAndStartAfterOrderByStart(
                        anyLong(), any(Status.class), any(LocalDateTime.class));
    }

    @Test
    void findAllByOwnerId_whenUserNotExist_assertThrowsUserNotFoundException() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(itemService, "userRepository", userRepository);
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        ReflectionTestUtils.setField(itemService, "repository", itemRepository);
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);
        assertThrows(UserNotFoundException.class,
                () -> itemService.findAllByOwnerId(generator.nextInt(), generator.nextInt(), generator.nextLong()));
        verify(itemRepository, never())
                .save(any(Item.class));
    }

    @Test
    void findAllByOwnerId() {
        ReflectionTestUtils.setField(itemService, "itemMapper", new ItemMapper());
        ReflectionTestUtils.setField(itemService, "commentMapper", new CommentMapper());
        ReflectionTestUtils.setField(itemService, "bookingMapper", new BookingMapper());
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(itemService, "userRepository", userRepository);
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        ReflectionTestUtils.setField(itemService, "repository", itemRepository);
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(itemService, "bookingRepository", bookingRepository);
        User owner = generator.nextObject(User.class);
        Item item = generator.nextObject(Item.class);
        item.setOwner(owner);
        next.setId(generator.nextLong());
        next.setItemId(item.getId());
        next.setBookerId(generator.nextLong());
        last.setId(generator.nextLong());
        last.setItemId(item.getId());
        last.setBookerId(generator.nextLong());
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRepository.findAllByOwnerIdWithComments(anyLong(), any(PageRequester.class)))
                .thenReturn(new PageImpl<>(List.of(item)));
        when(bookingRepository.findLastBookingsByItemOwnerId(anyLong()))
                .thenReturn(List.of(last));
        when(bookingRepository.findNextBookingsByItemOwnerId(anyLong()))
                .thenReturn(List.of(next));
        List<ItemDtoOut> foundList = itemService.findAllByOwnerId(from, limit, owner.getId());
        assertThat(foundList).hasSize(1);
        ItemDtoOut found = foundList.get(0);
        assertEquals(item.getId(), found.getId());
        assertEquals(item.getName(), found.getName());
        assertEquals(item.getDescription(), found.getDescription());
        assertEquals(item.getAvailable(), found.getAvailable());
        assertEquals(item.getRequest().getId(), found.getRequestId());
        assertEquals(last.getId(), found.getLastBooking().getId());
        assertEquals(last.getBookerId(), found.getLastBooking().getBookerId());
        assertEquals(next.getId(), found.getNextBooking().getId());
        assertEquals(next.getBookerId(), found.getNextBooking().getBookerId());
    }

    @Test
    void searchByNameOrDescription() {
        ReflectionTestUtils.setField(itemService, "itemMapper", new ItemMapper());
        ReflectionTestUtils.setField(itemService, "commentMapper", new CommentMapper());
        ReflectionTestUtils.setField(itemService, "bookingMapper", new BookingMapper());
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        ReflectionTestUtils.setField(itemService, "repository", itemRepository);
        Item item = generator.nextObject(Item.class);
        when(itemRepository.findAllByNameContainsIgnoreCaseOrDescriptionContainsIgnoreCaseAndAvailableIsTrue(
                anyString(), anyString(), any(PageRequester.class)))
                .thenReturn(new PageImpl<>(List.of(item)));
        List<ItemDtoOut> foundList = itemService.searchByNameOrDescription(from, limit, item.getDescription());
        assertThat(foundList).hasSize(1);
        ItemDtoOut found = foundList.get(0);
        assertEquals(item.getId(), found.getId());
        assertEquals(item.getName(), found.getName());
        assertEquals(item.getDescription(), found.getDescription());
        assertEquals(item.getAvailable(), found.getAvailable());
        assertEquals(item.getRequest().getId(), found.getRequestId());
        assertNull(found.getLastBooking());
        assertNull(found.getNextBooking());
    }

    @Test
    void createComment_whenCommentAuthorItemOwner_assertThrowsObjectOwnerException() {
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        ReflectionTestUtils.setField(itemService, "repository", itemRepository);
        CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
        ReflectionTestUtils.setField(itemService, "commentRepository", commentRepository);
        when(itemRepository.existsByIdAndOwnerId(anyLong(), anyLong()))
                .thenReturn(true);
        assertThrows(ObjectOwnerException.class,
                () -> itemService.createComment(
                        generator.nextLong(), generator.nextLong(), generator.nextObject(CommentDtoIn.class)));
        verify(commentRepository, never())
                .save(any(Comment.class));
    }

    @Test
    void createComment_whenCommentAuthorWithoutBooking_assertThrowsUnregisteredBookingException() {
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        ReflectionTestUtils.setField(itemService, "repository", itemRepository);
        CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
        ReflectionTestUtils.setField(itemService, "commentRepository", commentRepository);
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(itemService, "bookingRepository", bookingRepository);
        when(itemRepository.existsByIdAndOwnerId(anyLong(), anyLong()))
                .thenReturn(false);
        when(bookingRepository.existsBookingByItemIdAndBookerId(anyLong(), anyLong()))
                .thenReturn(false);
        assertThrows(UnregisteredBookingException.class,
                () -> itemService.createComment(generator.nextLong(), generator.nextLong(), generator.nextObject(CommentDtoIn.class)));
        verify(commentRepository, never())
                .save(any(Comment.class));
    }

    @Test
    void createComment() {
        ReflectionTestUtils.setField(itemService, "commentMapper", new CommentMapper());
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        ReflectionTestUtils.setField(itemService, "repository", itemRepository);
        CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
        ReflectionTestUtils.setField(itemService, "commentRepository", commentRepository);
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(itemService, "bookingRepository", bookingRepository);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(itemService, "userRepository", userRepository);
        CommentDtoIn dtoIn = generator.nextObject(CommentDtoIn.class);
        User user = generator.nextObject(User.class);
        Item item = generator.nextObject(Item.class);
        when(itemRepository.existsByIdAndOwnerId(anyLong(), anyLong()))
                .thenReturn(false);
        when(bookingRepository.existsBookingByItemIdAndBookerId(anyLong(), anyLong()))
                .thenReturn(true);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findByIdWithOwner(anyLong()))
                .thenReturn(Optional.of(item));
        when(commentRepository.save(any(Comment.class)))
                .then(returnsFirstArg());
        CommentDtoOut created = itemService.createComment(item.getId(), user.getId(), dtoIn);
        assertNull(created.getId());
        assertEquals(dtoIn.getText(), created.getText());
        assertEquals(user.getName(), created.getAuthorName());
        assertNotNull(created.getCreated());
        verify(commentRepository, times(1))
                .save(any(Comment.class));
    }
}
