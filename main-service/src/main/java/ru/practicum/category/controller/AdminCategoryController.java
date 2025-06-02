package ru.practicum.category.controller;

import jakarta.validation.Valid;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryFullDto;
import ru.practicum.category.dto.CategoryPostDto;
import ru.practicum.category.service.CategoryServiceImp;

@Validated
@Slf4j
@RestController
@RequestMapping("/admin/categories")
public class AdminCategoryController {
    private final CategoryServiceImp categoryService;
    public AdminCategoryController(CategoryServiceImp categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryFullDto createCategory(@Valid @RequestBody CategoryPostDto postDto) {
        log.info("Create new category {}", postDto);
        CategoryFullDto categoryFullDto = categoryService.addCategory(postDto);
        return categoryFullDto;
    }

    @PatchMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryFullDto updateCategory(@Valid @RequestBody CategoryPostDto postDto, @PathVariable("catId") long id) {
        log.info(String.format("Update category name: %s, id: %s ", postDto.getName(), id));
        CategoryFullDto categoryFullDto = categoryService.updateCategory(postDto, (int) id);
        return categoryFullDto;
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable("catId") long id) {
        log.info(String.format("Delete category: %s ", id));
        categoryService.deleteCategoryById((int) id);
    }

}
