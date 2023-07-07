package ru.practicum.shareit.abstraction.userreference.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import ru.practicum.shareit.abstraction.model.UserReference;

import java.util.List;

@NoRepositoryBean
public interface UserReferenceRepository<E extends UserReference> extends JpaRepository<E, Long> {

    List<E> findAllByUserId(Long userId);

    void deleteByUserId(Long userId);

}
