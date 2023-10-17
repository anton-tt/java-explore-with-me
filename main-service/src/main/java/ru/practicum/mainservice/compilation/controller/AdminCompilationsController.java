package ru.practicum.mainservice.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.compilation.dto.RequestCompilationDto;
import ru.practicum.mainservice.compilation.dto.ResponseCompilationDto;
import ru.practicum.mainservice.compilation.dto.UpdateRequestCompilationDto;
import ru.practicum.mainservice.compilation.service.CompilationService;
import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AdminCompilationsController {

    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseCompilationDto createCompilation(@RequestBody @Valid RequestCompilationDto compilationDto) {
        log.info("");
        log.info("Добавление новой побборки событий: {}", compilationDto);
        return compilationService.create(compilationDto);
    }

    @PatchMapping("/{compId}")
    public ResponseCompilationDto updateCompilation(@PathVariable Long compId,
                                                    @RequestBody @Valid UpdateRequestCompilationDto compilationDto) {
        log.info("");
        log.info("Обновление данных подборки событий с id = {}: {}", compId, compilationDto);
        return compilationService.update(compId, compilationDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long compId) {
        log.info("");
        log.info("Удаление всех данных подборки событий c id = {}", compId);
        compilationService.delete(compId);
    }

}