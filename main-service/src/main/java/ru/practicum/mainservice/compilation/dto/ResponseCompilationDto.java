package ru.practicum.mainservice.compilation.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.mainservice.event.dto.ShortResponseEventDto;
import java.util.List;

@Data
@Builder
public class ResponseCompilationDto {

    private Long id;
    private List<ShortResponseEventDto> events;
    private Boolean pinned;
    private String title;

}