package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.abstraction.userreference.repository.UserReferenceRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends UserReferenceRepository<Item> {

    void deleteByUserId(Long userId);

    List<Item> findAllByNameContainsIgnoreCaseOrDescriptionContainsIgnoreCaseAndAvailableIsTrue(
            String name, String description);

    @Query("select it from Item it " +
            "JOIN FETCH it.user " +
            "where it.id = ?1")
    Optional<Item> findByIdWithUser(Long itemId);

    @Query("select it from Item it " +
            "LEFT JOIN FETCH it.comments " +
            "where it.user.id = ?1")
    List<Item> findAllByUserIdWithComments(Long userId);

    @Query("select it from Item it " +
            "LEFT JOIN FETCH it.user " +
            "LEFT JOIN FETCH it.comments " +
            "where it.id = ?1")
    Optional<Item> findByIdWithUserAndComments(Long itemId);

}
