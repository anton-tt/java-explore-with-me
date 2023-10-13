package ru.practicum.mainservice.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.mainservice.request.status.RequestStatus;
import java.time.LocalDateTime;

@Data
@Builder
public class ResponseRequestDto {

    private Long id;
    private LocalDateTime created;
    private Long eventId;
    private Long requesterId;
    private RequestStatus status;

}