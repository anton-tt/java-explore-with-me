package ru.practicum.mainservice.compilation.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.mainservice.compilation.dto.RequestCompilationDto;
import ru.practicum.mainservice.compilation.dto.ResponseCompilationDto;
import ru.practicum.mainservice.compilation.model.Compilation;
import ru.practicum.mainservice.event.dto.ShortResponseEventDto;
import ru.practicum.mainservice.event.model.Event;
import java.util.List;

@UtilityClass
public class CompilationMapper {

    public Compilation toCompilation(RequestCompilationDto compilationDto, List<Event> events) {
        return Compilation.builder()
                .title(compilationDto.getTitle())
                .pinned(compilationDto.getPinned())
                .events(events)
                .build();
    }

    public ResponseCompilationDto toResponseCompilationDto(Compilation compilation,
                                                           List<ShortResponseEventDto> eventDtoList) {
        return ResponseCompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.getPinned())
                .events(eventDtoList)
                .build();
    }

}