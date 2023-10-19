package ru.practicum.mainservice.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.event.dto.RequestEventDto;
import ru.practicum.mainservice.event.dto.ResponseEventDto;
import ru.practicum.mainservice.event.dto.ShortResponseEventDto;
import ru.practicum.mainservice.event.dto.UpdateRequestEventDto;
import ru.practicum.mainservice.event.requestListDto.InitialRequestListDto;
import ru.practicum.mainservice.event.requestListDto.ResultRequestListDto;
import ru.practicum.mainservice.event.service.EventService;
import ru.practicum.mainservice.request.dto.ResponseRequestDto;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PrivateEventsController {

    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEventDto createEvent(@PathVariable @Positive Long userId,
                                        @RequestBody @Valid RequestEventDto eventDto) {
        log.info("");
        log.info("Добавление нового события: {}", eventDto);
        return eventService.create(userId, eventDto);
    }

    @GetMapping("/{id}")
    public ResponseEventDto getByIdByInitiator(@PathVariable @Positive Long userId,
                                               @PathVariable @Positive Long id) {
        log.info("");
        log.info("Получение данных события с id = {}", id);
        return eventService.getByIdByInitiator(userId, id);
    }

    @GetMapping
    public List<ShortResponseEventDto> getEventsOneInitiator(@PathVariable @Positive Long userId,
                                                             @RequestParam(required = false, defaultValue = "0")
                                                                 Integer from,
                                                             @RequestParam(required = false, defaultValue = "10")
                                                                 Integer size) {
        log.info("");
        log.info("Поиск всех событий, созданных пользователем с id = {}", userId);
        return eventService.getEventsOneInitiator(userId, from, size);
    }

    @PatchMapping("/{eventId}")
    public ResponseEventDto privateUpdateEvent(@PathVariable @Positive Long userId,
                                               @PathVariable @Positive Long eventId,
                                               @RequestBody @Valid UpdateRequestEventDto eventDto) {
        log.info("");
        log.info("Обновление данных события с id = {} в режиме private: {}", eventId, eventDto);
        return eventService.privateUpdate(userId, eventId, eventDto);
    }

    @PatchMapping("/{eventId}/requests")
    public ResultRequestListDto updateRequestsStatus(@PathVariable @Positive Long userId,
                                                     @PathVariable @Positive Long eventId,
                                                     @RequestBody InitialRequestListDto requestListDto) {
        log.info("");
        log.info("Обновление пользователем с id = {}, создавшим событие с id = {}, статусов запросов на участие в нём",
                userId, eventId);
        return eventService.updateRequestsStatus(userId, eventId, requestListDto);
    }

    @GetMapping("/{eventId}/requests")
    public List<ResponseRequestDto> getAllRequestOneEventFromInitiator(@PathVariable @Positive Long userId,
                                                                       @PathVariable @Positive Long eventId) {
        log.info("");
        log.info("Получение всех запросов пользователей на участие в событии с id = {}, " +
                "созданном инициатором с id = {}", eventId, userId);
        return eventService.getAllRequestOneEvent(userId, eventId);
    }

}