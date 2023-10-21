package ru.practicum.mainservice.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainservice.request.model.Request;
import ru.practicum.mainservice.request.status.RequestStatus;
import java.util.List;

public interface RequestRepository  extends JpaRepository<Request, Long> {


    Boolean existsByEventIdAndRequesterId(Long eventId, Long requesterId);

    List<Request> findAllByRequesterId(Long requesterId, Pageable pageable);

    List<Request> findAllByIdInAndStatusOrderByCreatedAsc(List<Long> requestIds, RequestStatus status);

    List<Request> findAllByEventId(Long eventId);

}