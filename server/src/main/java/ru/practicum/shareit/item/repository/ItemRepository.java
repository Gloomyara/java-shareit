package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findAllByNameContainsIgnoreCaseOrDescriptionContainsIgnoreCaseAndAvailableIsTrue(
            String name, String description, Pageable pageable);

    @Query("select it from Item it " +
            "JOIN FETCH it.owner " +
            "where it.id = ?1")
    Optional<Item> findByIdWithOwner(Long itemId);

    @Query("select it from Item it " +
            "LEFT JOIN FETCH it.owner " +
            "LEFT JOIN FETCH it.comments " +
            "where it.id = ?1")
    Optional<Item> findByIdWithOwnerAndComments(Long itemId);

    @EntityGraph(attributePaths = {"comments"})
    @Query("select it from Item it " +
            "where it.owner.id = ?1")
    Page<Item> findAllByOwnerIdWithComments(Long ownerId, Pageable pageable);

    boolean existsByIdAndOwnerId(Long itemId, Long ownerId);

    boolean existsByIdAndAvailableIsFalse(Long itemId);

}
