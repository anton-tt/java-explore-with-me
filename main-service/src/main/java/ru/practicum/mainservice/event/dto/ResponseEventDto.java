package ru.practicum.mainservice.event.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.mainservice.category.dto.ResponseCategoryDto;
import ru.practicum.mainservice.event.states.State;
import ru.practicum.mainservice.location.dto.ResponseLocationDto;
import ru.practicum.mainservice.user.dto.ShortResponseUserDto;
import java.time.LocalDateTime;

@Data
@Builder
public class ResponseEventDto {

    private Long id;
    private String title;
    private String annotation;
    private String description;
    private State state;
    private Boolean paid;
    private ResponseCategoryDto category;
    private ResponseLocationDto location;
    private ShortResponseUserDto initiator;
    private LocalDateTime createdOn;
    private LocalDateTime publishedOn;
    private LocalDateTime eventDate;
    private Integer participantLimit;
    private Integer confirmedRequests;
    private Boolean requestModeration;
    private Integer views;

}