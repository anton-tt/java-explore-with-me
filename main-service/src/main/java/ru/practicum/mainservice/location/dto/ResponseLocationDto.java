package ru.practicum.mainservice.location.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseLocationDto {

    private Double lat;
    private Double lon;

}