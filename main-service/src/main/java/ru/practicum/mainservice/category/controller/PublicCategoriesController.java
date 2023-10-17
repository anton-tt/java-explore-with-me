package ru.practicum.mainservice.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.category.dto.ResponseCategoryDto;
import ru.practicum.mainservice.category.service.CategoryService;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/categories")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PublicCategoriesController {

    private final CategoryService categoryService;

    @GetMapping("/{catId}")
    public ResponseCategoryDto getById(@PathVariable Long catId) {
        log.info("");
        log.info("Получение данных категории с id = {}", catId);
        return categoryService.getById(catId);
    }

    @GetMapping
    public List<ResponseCategoryDto> getAllCategories(@RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                      @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        log.info("");
        log.info("Поиск категорий по заданным параметрам");
        return categoryService.getAll(from, size);
    }

}