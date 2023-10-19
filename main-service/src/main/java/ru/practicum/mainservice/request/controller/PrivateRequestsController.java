package ru.practicum.mainservice.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.request.dto.ResponseRequestDto;
import ru.practicum.mainservice.request.service.RequestService;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PrivateRequestsController {

    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseRequestDto createRequest(@PathVariable @Positive Long userId,
                                            @RequestParam @Positive Long eventId) {
        log.info("");
        log.info("Добавление нового запроса на участие в событии с id = {} от пользователя с id = {}", userId, eventId);
        return requestService.create(userId, eventId);
    }

    @GetMapping
    public List<ResponseRequestDto> getRequestsOneRequester(@PathVariable @Positive Long userId,
                                                            @RequestParam(defaultValue = "0") Integer from,
                                                            @RequestParam(defaultValue = "10") Integer size) {
        log.info("");
        log.info("Поиск всех запросов на участие в событиях, оформленных пользователем с id = {}", userId);
        return requestService.getAllOneRequester(userId, from, size);
    }

    @PatchMapping("/{requestId}/cancel")
    public ResponseRequestDto cancelRequest(@PathVariable @Positive Long userId,
                                            @PathVariable @Positive Long requestId) {
        log.info("");
        log.info("Отмена запроса, с id = {} на участие в событии, пользователем с id = {}", requestId, userId);
        return requestService.cancelRequest(userId, requestId);
    }

}