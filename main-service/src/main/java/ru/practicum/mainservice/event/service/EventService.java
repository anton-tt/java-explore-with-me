package ru.practicum.mainservice.event.service;

import ru.practicum.mainservice.event.dto.RequestEventDto;
import ru.practicum.mainservice.event.dto.ResponseEventDto;

public interface EventService {

    ResponseEventDto create(Long userId, RequestEventDto eventDto);

}