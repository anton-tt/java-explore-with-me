package ru.practicum.mainservice.request.service;

import ru.practicum.mainservice.request.dto.ResponseRequestDto;
import java.util.List;

public interface RequestService {

    ResponseRequestDto create(Long userId, Long eventId);

    List<ResponseRequestDto> getAllOneRequester(Long requesterId, Integer from, Integer size);

    ResponseRequestDto cancelRequest(Long userId, Long requestId);

}