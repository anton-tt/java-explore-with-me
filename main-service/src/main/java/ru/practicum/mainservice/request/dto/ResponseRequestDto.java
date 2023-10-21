package ru.practicum.mainservice.request.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseRequestDto {

    private Long id;
    private String created;
    private Long event;
    private Long requester;
    private String status;

}