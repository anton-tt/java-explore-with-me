package ru.practicum.mainservice.location.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseLocationDto {

    //private Long id;
    private Double lat;
    private Double lon;

}