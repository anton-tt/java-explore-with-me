package ru.practicum.mainservice.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.compilation.dto.ResponseCompilationDto;
import ru.practicum.mainservice.compilation.service.CompilationService;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PublicCompilationsController {

    private final CompilationService compilationService;

    @GetMapping("/{compId}")
    public ResponseCompilationDto getCompilationByIdBy(@PathVariable Long compId) {
        log.info("");
        log.info("Получение данных подборки событий с id = {}", compId);
        return compilationService.getById(compId);
    }

    @GetMapping
    public List<ResponseCompilationDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                        @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                        @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        log.info("");
        log.info("Поиск подборок событий в соответствии с запросом пользователя");
        return compilationService.getCompilations(pinned, from, size);
    }

}