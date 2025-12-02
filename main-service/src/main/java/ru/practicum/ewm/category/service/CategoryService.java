package ru.practicum.ewm.category.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ObjectNotFoundException;
import ru.practicum.ewm.exception.UnboundCategoriesException;

import java.util.List;

@Service
@AllArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final CategoryMapper categoryMapper;

    @Transactional(readOnly = true)
    public CategoryDto getCategory(Long catId) {
        return categoryMapper.toCategoryDto(categoryRepository.findById(catId)
                .orElseThrow(() -> new ObjectNotFoundException("Category with id %d not found".formatted(catId))));
    }

    @Transactional
    public CategoryDto createCategory(NewCategoryDto category) {
        return categoryMapper.toCategoryDto(categoryRepository.save(categoryMapper.toCategory(category)));
    }

    @Transactional
    public void deleteCategory(Long catId) {
        Category category = categoryMapper.toCategory(getCategory(catId));
        List<Event> events = eventRepository.findByCategory(category);
        if (events.isEmpty()) {
            categoryRepository.delete(category);
        } else {
            throw new UnboundCategoriesException("The category is not empty");
        }
    }

    @Transactional
    public CategoryDto updateCategory(CategoryDto updateCategory, Long catId) {
        Category category = categoryMapper.toCategory(getCategory(catId));
        categoryMapper.updateCategory(updateCategory, category);
        return categoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories(Integer from, Integer size) {
        return categoryRepository.findAllWithOffset(from, size)
                .stream().map(categoryMapper::toCategoryDto).toList();
    }
}
