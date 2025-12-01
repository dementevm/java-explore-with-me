package ru.practicum.ewm.category.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.service.CategoryService;

@RestController
@AllArgsConstructor
@Validated
public class AdminCategoryController {
    private final CategoryService categoryService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/admin/categories")
    public CategoryDto createCategory(@Valid @RequestBody NewCategoryDto category) {
        return categoryService.createCategory(category);
    }

    @DeleteMapping("/admin/categories/{catId}")
    public ResponseEntity<String> deleteCategory(@PathVariable @Positive Long catId) {
        categoryService.deleteCategory(catId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Категория удалена");
    }

    @PatchMapping("/admin/categories/{catId}")
    public CategoryDto updateCategory(@PathVariable @Positive Long catId, @Valid @RequestBody CategoryDto category) {
        return categoryService.updateCategory(category, catId);
    }
}
