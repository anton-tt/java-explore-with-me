package ru.practicum.mainservice.category.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.mainservice.category.dto.ResponseCategoryDto;
import ru.practicum.mainservice.category.model.Category;

@UtilityClass
public class CategoryMapper {

    public ResponseCategoryDto toResponseCategoryDto(Category category) {
        return ResponseCategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

}