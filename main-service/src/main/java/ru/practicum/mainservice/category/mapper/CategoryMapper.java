package ru.practicum.mainservice.category.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.mainservice.category.dto.RequestCategoryDto;
import ru.practicum.mainservice.category.dto.ResponseCategoryDto;
import ru.practicum.mainservice.category.model.Category;

@UtilityClass
public class CategoryMapper {

    public Category toCategory(RequestCategoryDto categoryDto) {
        return Category.builder()
                .name(categoryDto.getName())
                .build();
    }

    public ResponseCategoryDto toResponseCategoryDto(Category category) {
        return ResponseCategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public Category toUpdateCategory(Category category, RequestCategoryDto newData) {
        return Category.builder()
                .id(category.getId())
                .name(newData.getName())
                .build();
    }

}