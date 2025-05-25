package ru.practicum.category.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryFullDto;
import ru.practicum.category.dto.CategoryPostDto;
import ru.practicum.category.service.CategoryServiceImp;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/categories")
public class PublicCategoryController {
    private final CategoryServiceImp categoryService;
    public PublicCategoryController(CategoryServiceImp categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/{catId}")
    public CategoryFullDto getCategoryById (@PathVariable("catId") long id) {
        log.info("Get category {}", id);
        CategoryFullDto categoryFullDto = categoryService.getCategoryById((int) id);
        return categoryFullDto;
    }

    @GetMapping
    public List<CategoryFullDto> getAllCategories (@RequestParam(required = false) Integer from, @RequestParam(required = false) Integer size){
        log.info(String.format("Get all categories from: %s, size: %s", from, size));
        List<CategoryFullDto> categories = categoryService.getAllCategories(from != null ? from : 0, size != null ? size : 10);
        return categories;
    }
}
