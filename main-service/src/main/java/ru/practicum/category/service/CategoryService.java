package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryFullDto;
import ru.practicum.category.dto.CategoryPostDto;

import java.util.List;

public interface CategoryService {
    public CategoryFullDto addCategory(CategoryPostDto categoryDto);
    public CategoryFullDto updateCategory(CategoryPostDto categoryDto, Integer id);
    public CategoryFullDto getCategoryById(Integer id);
    public List<CategoryFullDto> getAllCategories(Integer from, Integer size);
    public void deleteCategoryById(Integer id);
}
