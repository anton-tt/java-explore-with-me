package ru.practicum.mainservice.event.service;

import ru.practicum.mainservice.event.dto.RequestEventDto;
import ru.practicum.mainservice.event.dto.ResponseEventDto;
import ru.practicum.mainservice.event.dto.ShortResponseEventDto;
import ru.practicum.mainservice.event.dto.UpdateRequestEventDto;
import ru.practicum.mainservice.event.requestListDto.InitialRequestListDto;
import ru.practicum.mainservice.event.requestListDto.ResultRequestListDto;
import ru.practicum.mainservice.request.dto.ResponseRequestDto;
import ru.practicum.mainservice.user.model.User;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    ResponseEventDto create(Long userId, RequestEventDto eventDto);

    ResponseEventDto getById(Long id);

    ResponseEventDto getByIdByInitiator(Long userId, Long id);

    List<ShortResponseEventDto> getEventsOneInitiator(Long userId, Integer from, Integer size);

    List<ResponseEventDto> getFullEvents(List<User> users, List<String> states, List<Long> categories,
                                         LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    List<ShortResponseEventDto> getPublishedEvents(String text, List<Long> categories, Boolean paid,
                                                   LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                   Boolean onlyAvailable, String sort, Integer from,
                                                   Integer size, HttpServletRequest requestData);

    ResponseEventDto privateUpdate(Long userId, Long id, UpdateRequestEventDto eventDto);

    ResponseEventDto adminUpdate(Long id, UpdateRequestEventDto eventDto);

    ResultRequestListDto updateRequestsStatus(Long userId, Long eventId, InitialRequestListDto requestListDto);

    List<ResponseRequestDto> getAllRequestOneEvent(Long userId, Long eventId);

}