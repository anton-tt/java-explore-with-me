package ru.practicum.mainservice.category.service;

import ru.practicum.mainservice.category.dto.RequestCategoryDto;
import ru.practicum.mainservice.category.dto.ResponseCategoryDto;
import java.util.List;

public interface CategoryService {

   ResponseCategoryDto create(RequestCategoryDto categoryDto);

   ResponseCategoryDto getById(Long catId);

   List<ResponseCategoryDto> getAll(Integer from, Integer size);

   ResponseCategoryDto update(Long catId, RequestCategoryDto categoryDto);

   void delete(long catId);

}