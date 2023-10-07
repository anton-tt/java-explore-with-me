package ru.practicum.mainservice.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainservice.event.model.Event;

public interface EventRepository extends JpaRepository<Event, Long> {

}