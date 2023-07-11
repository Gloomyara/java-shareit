package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerId(Long userId);

    void deleteByOwnerId(Long userId);

    List<Item> findAllByNameContainsIgnoreCaseOrDescriptionContainsIgnoreCaseAndAvailableIsTrue(
            String name, String description);

    @Query("select it from Item it " +
            "JOIN FETCH it.owner " +
            "where it.id = ?1")
    Optional<Item> findByIdWithOwner(Long itemId);

    @Query("select it from Item it " +
            "LEFT JOIN FETCH it.comments " +
            "where it.owner.id = ?1")
    List<Item> findAllByOwnerIdWithComments(Long userId);

    @Query("select it from Item it " +
            "LEFT JOIN FETCH it.owner " +
            "LEFT JOIN FETCH it.comments " +
            "where it.id = ?1")
    Optional<Item> findByIdWithOwnerAndComments(Long itemId);

}
