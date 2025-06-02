package ru.practicum.category.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.category.dto.CategoryFullDto;
import ru.practicum.category.dto.CategoryPostDto;
import ru.practicum.category.model.Category;

@Component
public class CategoryMapper {
    public Category toCategory(CategoryPostDto postDto) {
        Category category = new Category();
        category.setName(postDto.getName());
        return category;
    }

    public CategoryFullDto toCategoryPostFullDto(Category category) {
        CategoryFullDto categoryFullDto = new CategoryFullDto();
        categoryFullDto.setName(category.getName());
        categoryFullDto.setId(category.getId());
        return categoryFullDto;
    }
}
