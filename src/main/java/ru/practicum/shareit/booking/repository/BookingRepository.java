package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.abstraction.userreference.repository.UserReferenceRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingShort;
import ru.practicum.shareit.booking.model.Status;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends UserReferenceRepository<Booking> {

    String bookingJql = "select b from Booking b " +
            "JOIN FETCH b.user " +
            "JOIN FETCH b.item ";

    @Query(bookingJql + "where b.id = ?1 ")
    Optional<Booking> findByIdWithUserAndItem(Long bookingId);

    @Query("select case when (count(b) > 0) then true else false end " +
            "from Booking b " +
            "where b.item.id = ?1 " +
            "and b.user.id = ?2 " +
            "and b.status = 'APPROVED' " +
            "and b.end < CURRENT_TIMESTAMP ")
    boolean existsBookingByItemIdAndUserId(Long itemId, Long userId);

    Optional<Booking> findBookingByUserIdAndItemIdAndStatus(Long userId, Long itemId, Status status);

    @Query(bookingJql + "where b.user.id = ?1")
    List<Booking> findAllByUserId(Long userId, Sort sort);

    @Query(bookingJql + "where b.item.user.id = ?1")
    List<Booking> findAllByOwnerId(Long userId, Sort sort);

    @Query(bookingJql + "where b.item.user.id = ?1 " +
            "and b.start > CURRENT_TIMESTAMP")
    List<Booking> findAllByOwnerIdWhereStartInFuture(Long userId, Sort sort);

    @Query(bookingJql +
            "where b.item.user.id = ?1 " +
            "and b.end < CURRENT_TIMESTAMP")
    List<Booking> findAllByOwnerIdWhereEndInPast(Long userId, Sort sort);

    @Query(bookingJql +
            "where b.user.id = ?1 " +
            "and b.start > CURRENT_TIMESTAMP")
    List<Booking> findAllByUserIdWhereStartInFuture(Long userId, Sort sort);

    @Query(bookingJql + "where b.user.id = ?1 " +
            "and b.end < CURRENT_TIMESTAMP")
    List<Booking> findAllByUserIdWhereEndInPast(Long userId, Sort sort);

    @Query(bookingJql + "where b.user.id = ?1 " +
            "and CURRENT_TIMESTAMP between b.start and b.end")
    List<Booking> findAllByUserIdWithTimestampsBetweenStartAndEnd(Long userId, Sort sort);

    @Query(bookingJql + "where b.item.user.id = ?1 " +
            "and CURRENT_TIMESTAMP between b.start and b.end")
    List<Booking> findAllByOwnerIdTimestampsBetweenStartAndEnd(Long userId, Sort sort);

    @Query(bookingJql + "where b.user.id = ?1 " +
            "and b.status = ?2")
    List<Booking> findBookingsByUserIdAndStatus(Long userId, Status status, Sort sort);

    @Query(bookingJql + "where b.item.user.id = ?1 " +
            "and b.status = ?2")
    List<Booking> findBookingsByOwnerIdAndStatus(Long userId, Status status, Sort sort);

    @Query(value = "select id, user_id as bookerId, item_id as itemId " +
            "from booking " +
            "where item_id = ?1 " +
            "and status = 'APPROVED' " +
            "and start_time <= CURRENT_TIMESTAMP " +
            "order by start_time desc " +
            "limit 1 ", nativeQuery = true)
    Optional<BookingShort> findLastBookingByItemId(Long itemId);

    @Query(value = "select id, user_id as bookerId, item_id as itemId " +
            "from booking " +
            "where item_id = ?1 " +
            "and status = 'APPROVED' " +
            "and start_time >= CURRENT_TIMESTAMP " +
            "order by start_time " +
            "limit 1 ", nativeQuery = true)
    Optional<BookingShort> findNextBookingByItemId(Long itemId);

    @Query(nativeQuery = true,
            value = "select b.id as id, b.user_id as bookerId, b.item_id as itemId " +
                    "from booking b " +
                    "join (select item_id, MAX(start_time) max " +
                    "from booking " +
                    "where item_id in (select id from item " +
                    "where item.user_id = ?1) " +
                    "and status = 'APPROVED' " +
                    "and start_time <= CURRENT_TIMESTAMP " +
                    "group by item_id) as l on b.item_id = l.item_id " +
                    "and b.start_time = l.max")
    List<BookingShort> findLastBookingsByUserId(Long userId);

    @Query(nativeQuery = true,
            value = "select b.id as id, b.user_id as bookerId, b.item_id as itemId " +
                    "from booking b " +
                    "join (select item_id, MIN(start_time) min " +
                    "from booking " +
                    "where item_id in (select id from item " +
                    "where item.user_id = ?1) " +
                    "and status = 'APPROVED' " +
                    "and start_time >= CURRENT_TIMESTAMP " +
                    "group by item_id) as n on b.item_id = n.item_id " +
                    "and b.start_time = n.min")
    List<BookingShort> findNextBookingsByUserId(Long userId);
}
