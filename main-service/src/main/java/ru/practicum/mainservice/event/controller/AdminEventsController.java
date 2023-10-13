package ru.practicum.mainservice.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.event.dto.ResponseEventDto;
import ru.practicum.mainservice.event.dto.UpdateRequestEventDto;
import ru.practicum.mainservice.event.service.EventService;
import ru.practicum.mainservice.user.model.User;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AdminEventsController {

    private final EventService eventService;

    @GetMapping
    public List<ResponseEventDto> getFullEvents(@RequestParam(required = false) List<User> users,
                                                @RequestParam(required = false) List<String> states,
                                                @RequestParam(required = false) List<Long> categories,
                                                @RequestParam(required = false)
                                                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                @RequestParam(required = false)
                                                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        log.info("");
        log.info("Поиск полной информации о событиях по запросу администратора");
        return eventService.getFullEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public ResponseEventDto adminUpdateEvent(@PathVariable Long eventId,
                                             @RequestBody @Valid UpdateRequestEventDto eventDto) {
        log.info("");
        log.info("Обновление в режиме admin данных события с id = {}: {}", eventId, eventDto);
        return eventService.adminUpdate(eventId, eventDto);
    }

}