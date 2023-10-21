package ru.practicum.mainservice.compilation.service;

import ru.practicum.mainservice.compilation.dto.RequestCompilationDto;
import ru.practicum.mainservice.compilation.dto.ResponseCompilationDto;
import ru.practicum.mainservice.compilation.dto.UpdateRequestCompilationDto;
import java.util.List;

public interface CompilationService {

    ResponseCompilationDto create(RequestCompilationDto compilationDto);

    ResponseCompilationDto getById(Long compId);

    List<ResponseCompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);

    ResponseCompilationDto update(Long compId, UpdateRequestCompilationDto compilationDto);

    void delete(Long compId);

}