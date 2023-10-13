package ru.practicum.mainservice.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PrivateEventsController {

    private final EventService eventService;

    @PostMapping
    public ResponseEventDto createEvent(@PathVariable Long userId,
                                        @RequestBody @Valid RequestEventDto eventDto) {
        log.info("");
        log.info("Добавление нового события: {}", eventDto);
        return eventService.create(userId, eventDto);
    }

    @GetMapping("/{id}")
    public ResponseEventDto getByIdByInitiator(@PathVariable Long userId,
                                               @PathVariable Long id) {
        log.info("");
        log.info("Получение данных события с id = {}", id);
        return eventService.getByIdByInitiator(userId, id);
    }

    @GetMapping
    public List<ShortResponseEventDto> getEventsOneInitiator(@PathVariable Long userId,
                                                             @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                             @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        log.info("");
        log.info("Поиск всех событий, созданных пользователем с id = {}", userId);
        return eventService.getEventsOneInitiator(userId, from, size);
    }

    @PatchMapping("/{eventId}")
    public ResponseEventDto privateUpdateEvent(@PathVariable Long userId,
                                               @PathVariable Long eventId,
                                               @RequestBody @Valid UpdateRequestEventDto eventDto) {
        log.info("");
        log.info("Обновление в режиме private данных события с id = {}: {}", eventId, eventDto);
        return eventService.privateUpdate(userId, eventId, eventDto);
    }

    @PatchMapping("/{eventId}/requests")
    public ResultRequestListDto updateRequestsStatus(@PathVariable Long userId,
                                                     @PathVariable Long eventId,
                                                     @RequestBody InitialRequestListDto requestListDto) {
        log.info("");
        log.info("Обновление пользователем с id = {}, создавшим событие с id = {}, статусов запросов на участие в нём",
                userId, eventId);
        return eventService.updateRequestsStatus(userId, eventId, requestListDto);
    }

    @GetMapping("/{eventId}/requests")
    public List<ResponseRequestDto> getAllRequestOneEventFromInitiator(@PathVariable Long userId,
                                                                       @PathVariable Long eventId) {
        log.info("");
        log.info("Получение всех запросов пользователей на участие в событии с id = {}, " +
                "созданном инициатором с id = {}", eventId, userId);
        return eventService.getAllRequestOneEvent(userId, eventId);
    }

}