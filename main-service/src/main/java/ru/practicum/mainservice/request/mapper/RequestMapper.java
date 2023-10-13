package ru.practicum.mainservice.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.request.dto.ResponseRequestDto;
import ru.practicum.mainservice.request.model.Request;
import ru.practicum.mainservice.request.status.RequestStatus;
import ru.practicum.mainservice.user.model.User;
import java.time.LocalDateTime;

@UtilityClass
public class RequestMapper {

    public Request toRequest(User requester, Event event, LocalDateTime currentMoment, RequestStatus requestStatus) {
        return Request.builder()
                .created(currentMoment)
                .requester(requester)
                .event(event)
                .status(requestStatus)
                .build();
    }

    public ResponseRequestDto toResponseRequestDto(Request request) {
        return ResponseRequestDto.builder()
                .id(request.getId())
                .created(request.getCreated())
                .requesterId(request.getRequester().getId())
                .eventId(request.getEvent().getId())
                .build();
    }

}