package ru.practicum.mainservice.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.category.dto.RequestCategoryDto;
import ru.practicum.mainservice.category.dto.ResponseCategoryDto;
import ru.practicum.mainservice.category.mapper.CategoryMapper;
import ru.practicum.mainservice.category.model.Category;
import ru.practicum.mainservice.category.repository.CategoryRepository;
import ru.practicum.mainservice.event.repository.EventRepository;
import ru.practicum.mainservice.exception.DataConflictException;
import ru.practicum.mainservice.exception.NotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    private Category getCategoryById(long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Категория " +
                "с id = %s отсутствует в БД. Выполнить операцию невозможно!", id)));
    }

    @Override
    public ResponseCategoryDto create(RequestCategoryDto categoryDto) {
        Category category = categoryRepository.save(CategoryMapper.toCategory(categoryDto));
        log.info("Данные новой категории добавлены в БД: {}.", category);
        ResponseCategoryDto responseCategoryDto = CategoryMapper.toResponseCategoryDto(category);
        log.info("Новая категория создана: {}.", responseCategoryDto);
        return responseCategoryDto;
    }

    @Override
    public ResponseCategoryDto getById(Long catId) {
        Category category = getCategoryById(catId);
        log.info("Данные категории найдены в БД: {}.", category);
        ResponseCategoryDto responseCategoryDto = CategoryMapper.toResponseCategoryDto(category);
        log.info("Запрашиваемая атегория найдена: {}.", responseCategoryDto);
        return responseCategoryDto;
    }

    @Override
    public List<ResponseCategoryDto> getAll(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Category> categoryList = categoryRepository.findAll(pageable).getContent();
        log.info("Данные категорий найдены в БД.");
        List<ResponseCategoryDto> categoryDtoList = new ArrayList<>();
        if (!categoryList.isEmpty()) {
            categoryDtoList = categoryList
                    .stream()
                    .map(CategoryMapper::toResponseCategoryDto)
                    .collect(Collectors.toList());
        }
        log.info("Сформирован список имеющихся категорий в соответствии с поставленным запросом в количестве {}.",
                categoryDtoList.size());
        return categoryDtoList;
    }

    @Override
    public ResponseCategoryDto update(Long catId, RequestCategoryDto categoryDto) {
        Category oldCategory = getCategoryById(catId);
        Category category = categoryRepository.save(CategoryMapper.toUpdateCategory(oldCategory, categoryDto));
        log.info("Данные категории обновлены в БД: {}.", category);
        ResponseCategoryDto responseCategoryDto = CategoryMapper.toResponseCategoryDto(category);
        log.info("Категория обновлена: {}.", responseCategoryDto);
        return responseCategoryDto;
    }

    @Override
    public void delete(long catId) {
        Category category = getCategoryById(catId);
        if (eventRepository.existsByCategoryId(catId)) {
            throw new DataConflictException("Категория используется для описания события. Выполнить операцию невозможно!");
        }
        log.info("Категория найдена в БД: {}.", category);
        categoryRepository.delete(category);
        log.info("Все данные категории удалены.");
    }

}