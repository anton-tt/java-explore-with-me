package ru.practicum.mainservice.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.category.dto.RequestCategoryDto;
import ru.practicum.mainservice.category.dto.ResponseCategoryDto;
import ru.practicum.mainservice.category.service.CategoryService;
import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AdminCategoriesController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseCategoryDto createCategory(@RequestBody @Valid RequestCategoryDto categoryDto) {
        log.info("");
        log.info("Добавление новой категории: {}", categoryDto);
        return categoryService.create(categoryDto);
    }

    @PatchMapping("/{catId}")
    public ResponseCategoryDto updateCategory(@PathVariable Long catId,
                                              @RequestBody @Valid RequestCategoryDto categoryDto) {
        log.info("");
        log.info("Обновление данных категории с id = {}: {}", catId, categoryDto);
        return categoryService.update(catId, categoryDto);
    }


    @DeleteMapping("/{catId}")
    public void deleteCategory(@PathVariable Long catId) {
        log.info("");
        log.info("Удаление всех данных категории c id = {}", catId);
        categoryService.delete(catId);
    }

}