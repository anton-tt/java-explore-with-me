package ru.practicum.mainservice.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.event.dto.ResponseEventDto;
import ru.practicum.mainservice.event.dto.ShortResponseEventDto;
import ru.practicum.mainservice.event.service.EventService;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PublicEventsController {

    private final EventService eventService;

    @GetMapping("/{eventId}")
    public ResponseEventDto getById(@PathVariable @Positive Long eventId,
                                    HttpServletRequest requestData) {
        log.info("");
        log.info("Получение данных события с id = {}", eventId);
        return eventService.getById(eventId, requestData);
    }

    @GetMapping
    public List<ShortResponseEventDto> getPublishedEvents(@RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                          @RequestParam(defaultValue = "10") @Min(1) Integer size,
                                                          @RequestParam(required = false) String text,
                                                          @RequestParam(required = false) List<Long> categories,
                                                          @RequestParam(required = false) Boolean paid,
                                                          @RequestParam(required = false)
                                                          @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                              LocalDateTime rangeStart,
                                                          @RequestParam(required = false)
                                                          @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                              LocalDateTime rangeEnd,
                                                          @RequestParam(required = false) Boolean onlyAvailable,
                                                          @RequestParam(required = false) String sort,
                                                          HttpServletRequest requestData) {
        log.info("");
        log.info("Поиск опубликованных событий по запросу пользователя");
        return eventService.getPublishedEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort,
                from, size, requestData);
    }

}