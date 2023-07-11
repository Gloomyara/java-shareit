package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    String bookingJql = "select b from Booking b " +
            "JOIN FETCH b.booker " +
            "JOIN FETCH b.item ";

    List<Booking> findAllByBookerId(Long bookerId);

    void deleteByBookerId(Long bookerId);

    @Query(bookingJql + "where b.id = ?1 ")
    Optional<Booking> findByIdWithBookerAndItem(Long bookingId);

    @Query("select case when (count(b) > 0) then true else false end " +
            "from Booking b " +
            "where b.item.id = ?1 " +
            "and b.booker.id = ?2 " +
            "and b.status = 'APPROVED' " +
            "and b.end < CURRENT_TIMESTAMP")
    boolean existsBookingByItemIdAndBookerId(Long itemId, Long bookerId);

    Optional<Booking> findBookingByBookerIdAndItemIdAndStatus(Long bookerId, Long itemId, Status status);

    @Query(bookingJql + "where b.booker.id = ?1")
    List<Booking> findAllByBookerId(Long bookerId, Sort sort);

    @Query(bookingJql + "where b.item.owner.id = ?1")
    List<Booking> findAllByOwnerId(Long itemOwnerId, Sort sort);

    @Query(bookingJql + "where b.item.owner.id = ?1 " +
            "and b.start > CURRENT_TIMESTAMP")
    List<Booking> findAllByOwnerIdWhereStartInFuture(Long itemOwnerId, Sort sort);

    @Query(bookingJql +
            "where b.item.owner.id = ?1 " +
            "and b.end < CURRENT_TIMESTAMP")
    List<Booking> findAllByOwnerIdWhereEndInPast(Long itemOwnerId, Sort sort);

    @Query(bookingJql +
            "where b.booker.id = ?1 " +
            "and b.start > CURRENT_TIMESTAMP")
    List<Booking> findAllByBookerIdWhereStartInFuture(Long bookerId, Sort sort);

    @Query(bookingJql + "where b.booker.id = ?1 " +
            "and b.end < CURRENT_TIMESTAMP")
    List<Booking> findAllByBookerIdWhereEndInPast(Long bookerId, Sort sort);

    @Query(bookingJql + "where b.booker.id = ?1 " +
            "and CURRENT_TIMESTAMP between b.start and b.end")
    List<Booking> findAllByBookerIdWithTimestampsBetweenStartAndEnd(Long bookerId, Sort sort);

    @Query(bookingJql + "where b.item.owner.id = ?1 " +
            "and CURRENT_TIMESTAMP between b.start and b.end")
    List<Booking> findAllByOwnerIdWithTimestampsBetweenStartAndEnd(Long itemOwnerId, Sort sort);

    @Query(bookingJql + "where b.booker.id = ?1 " +
            "and b.status = ?2")
    List<Booking> findBookingsByBookerIdAndStatus(Long bookerId, Status status, Sort sort);

    @Query(bookingJql + "where b.item.owner.id = ?1 " +
            "and b.status = ?2")
    List<Booking> findBookingsByOwnerIdAndStatus(Long itemOwnerId, Status status, Sort sort);

    Optional<BookingShort> findTopByItemIdAndStatusAndStartBeforeOrderByStartDesc(
            Long itemId, Status status, LocalDateTime time);

    Optional<BookingShort> findTopByItemIdAndStatusAndStartAfterOrderByStart(
            Long itemId, Status status, LocalDateTime time);

    @Query(nativeQuery = true,
            value = "select b.id as id, b.booker_id as bookerId, b.item_id as itemId " +
                    "from booking b " +
                    "join (select item_id, MAX(start_time) max " +
                    "from booking " +
                    "where item_id in (select id from item " +
                    "where item.owner_id = ?1) " +
                    "and status = 'APPROVED' " +
                    "and start_time <= CURRENT_TIMESTAMP " +
                    "group by item_id) as l on b.item_id = l.item_id " +
                    "and b.start_time = l.max")
    List<BookingShort> findLastBookingsByOwnerId(Long itemOwnerId, List<Long> ItemIds);

    @Query(nativeQuery = true,
            value = "select b.id as id, b.booker_id as bookerId, b.item_id as itemId " +
                    "from booking b " +
                    "join (select item_id, MIN(start_time) min " +
                    "from booking " +
                    "where item_id in (select id from item " +
                    "where item.owner_id = ?1) " +
                    "and status = 'APPROVED' " +
                    "and start_time >= CURRENT_TIMESTAMP " +
                    "group by item_id) as n on b.item_id = n.item_id " +
                    "and b.start_time = n.min")
    List<BookingShort> findNextBookingsByOwnerId(Long itemOwnerId, List<Long> ItemIds);
}
