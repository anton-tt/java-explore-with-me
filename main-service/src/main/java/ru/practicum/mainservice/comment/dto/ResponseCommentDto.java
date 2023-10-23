package ru.practicum.mainservice.comment.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.mainservice.event.dto.ShortResponseEventDto;
import ru.practicum.mainservice.user.dto.ShortResponseUserDto;
import java.time.LocalDateTime;

@Data
@Builder
public class ResponseCommentDto {

    private Long id;
    private String text;
    private String state;
    private ShortResponseUserDto author;
    private ShortResponseEventDto event;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
    private LocalDateTime publishedOn;

}