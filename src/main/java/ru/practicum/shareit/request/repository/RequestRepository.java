package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.Request;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {

    String requestJql = "select r from Request r " +
            "LEFT OUTER JOIN FETCH r.items ";

    @Query(requestJql + "where r.id = ?1")
    Optional<Request> findByIdWithItems(Long requestId);

    @Query(requestJql + "where r.author.id = ?1")
    List<Request> findAllByAuthorIdWithItems(Long authorId);

    @EntityGraph(attributePaths = {"items"})
    @Query("select r from Request r " +
            "where r.author.id not in (?1) ")
    Page<Request> findAllOtherRequests(Long authorId, Pageable pageable);

}