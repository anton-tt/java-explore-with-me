package ru.practicum.mainservice.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import ru.practicum.mainservice.category.dto.ResponseCategoryDto;
import ru.practicum.mainservice.user.dto.ShortResponseUserDto;
import java.time.LocalDateTime;

@Data
@Builder
public class ShortResponseEventDto {

    private Long id;
    private String annotation;
    private ResponseCategoryDto category;
    private Integer confirmedRequests;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private ShortResponseUserDto initiator;
    private Boolean paid;
    private String title;
    private Integer views;

}