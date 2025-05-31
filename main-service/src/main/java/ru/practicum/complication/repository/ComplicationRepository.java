package ru.practicum.complication.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.complication.model.Complication;

import java.util.List;

@Repository
public interface ComplicationRepository extends JpaRepository<Complication, Long> {
    List<Complication> findAllByPinned(Boolean pinned, Pageable page);
}
