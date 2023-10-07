package ru.practicum.mainservice.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.event.dto.RequestEventDto;
import ru.practicum.mainservice.event.dto.ResponseEventDto;
import ru.practicum.mainservice.event.service.EventService;
import javax.validation.Valid;

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

}