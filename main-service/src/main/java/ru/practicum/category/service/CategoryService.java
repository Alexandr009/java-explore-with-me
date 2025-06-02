package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryFullDto;
import ru.practicum.category.dto.CategoryPostDto;

import java.util.List;

public interface CategoryService {
    CategoryFullDto addCategory(CategoryPostDto categoryDto);

    CategoryFullDto updateCategory(CategoryPostDto categoryDto, Integer id);

    CategoryFullDto getCategoryById(Integer id);

    List<CategoryFullDto> getAllCategories(Integer from, Integer size);

    void deleteCategoryById(Integer id);
}
