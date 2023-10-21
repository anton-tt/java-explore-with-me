package ru.practicum.mainservice.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainservice.event.model.Event;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    Boolean existsByCategoryId(Long catId);

    List<Event> findAllByIdIn(List<Long> eventIds);

    List<Event> findAll(Specification<Event> specification, Pageable pageable);

}