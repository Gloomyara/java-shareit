package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
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

    @Query(bookingJql + "where b.id = ?1")
    Optional<Booking> findByIdWithBookerAndItem(Long bookingId);

    @Query("select case when (count(b) > 0) then true else false end " +
            "from Booking b " +
            "where b.item.id = ?1 " +
            "and b.booker.id = ?2 " +
            "and b.status = 'APPROVED' " +
            "and b.end < CURRENT_TIMESTAMP")
    boolean existsBookingByItemIdAndBookerId(Long itemId, Long bookerId);

    @Query("select case when (count(b) > 0) then true else false end " +
            "from Booking b " +
            "where b.id = ?1 " +
            "and (b.booker.id = ?2 OR b.item.owner.id = ?2)")
    boolean existsByBookingIdAndBookerOrItemOwnerId(Long bookingId, Long userId);

    boolean existsByIdAndItemOwnerId(Long bookingId, Long itemOwnerId);

    boolean existsByIdAndStatus(Long bookingId, Status status);

    boolean existsByIdAndBookerId(Long bookingId, Long bookerId);

    @EntityGraph(attributePaths = {"item", "item.owner"})
    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1")
    Page<Booking> findAllByItemOwnerId(Long itemOwnerId, Pageable pageable);

    @EntityGraph(attributePaths = {"item", "booker"})
    @Query("select b from Booking b " +
            "where b.booker.id = ?1 ")
    Page<Booking> findAllByBookerId(Long bookerId, Pageable pageable);

    @EntityGraph(attributePaths = {"item", "item.owner"})
    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 " +
            "and b.start > CURRENT_TIMESTAMP")
    Page<Booking> findAllByItemOwnerIdWhereStartInFuture(Long itemOwnerId, Pageable pageable);

    @EntityGraph(attributePaths = {"item", "item.owner"})
    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 " +
            "and b.end < CURRENT_TIMESTAMP")
    Page<Booking> findAllByItemOwnerIdWhereEndInPast(Long itemOwnerId, Pageable pageable);

    @EntityGraph(attributePaths = {"item", "item.owner"})
    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 " +
            "and CURRENT_TIMESTAMP between b.start and b.end")
    Page<Booking> findAllByItemOwnerIdWithTimestampsBetweenStartAndEnd(Long itemOwnerId, Pageable pageable);

    @EntityGraph(attributePaths = {"item", "booker"})
    @Query("select b from Booking b " +
            "where b.start > CURRENT_TIMESTAMP " +
            "and b.booker.id = ?1 ")
    Page<Booking> findAllByBookerIdWhereStartInFuture(Long bookerId, Pageable pageable);

    @EntityGraph(attributePaths = {"item", "booker"})
    @Query("select b from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.end < CURRENT_TIMESTAMP")
    Page<Booking> findAllByBookerIdWhereEndInPast(Long bookerId, Pageable pageable);

    @EntityGraph(attributePaths = {"item", "booker"})
    @Query("select b from Booking b " +
            "where b.booker.id = ?1 " +
            "and CURRENT_TIMESTAMP between b.start and b.end")
    Page<Booking> findAllByBookerIdWithTimestampsBetweenStartAndEnd(Long bookerId, Pageable pageable);

    @EntityGraph(attributePaths = {"item", "booker"})
    @Query("select b from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.status = ?2")
    Page<Booking> findBookingsByBookerIdAndStatus(Long bookerId, Status status, Pageable pageable);

    @EntityGraph(attributePaths = {"item", "item.owner"})
    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 " +
            "and b.status = ?2")
    Page<Booking> findBookingsByItemOwnerIdAndStatus(Long itemOwnerId, Status status, Pageable pageable);

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
    List<BookingShort> findLastBookingsByItemOwnerId(Long itemOwnerId);

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
    List<BookingShort> findNextBookingsByItemOwnerId(Long itemOwnerId);
}
