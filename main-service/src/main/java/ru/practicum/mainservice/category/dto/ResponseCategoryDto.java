package ru.practicum.mainservice.category.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseCategoryDto {

    private Long id;
    private String name;

}