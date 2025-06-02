package ru.practicum.category.service;

import org.springframework.stereotype.Service;
import ru.practicum.category.dto.CategoryFullDto;
import ru.practicum.category.dto.CategoryPostDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImp implements CategoryService {
    private static CategoryRepository categoryRepository;
    private static CategoryMapper categoryMapper;
    private static EventRepository eventRepository;
    public CategoryServiceImp(CategoryRepository categoryRepository, CategoryMapper categoryMapper, EventRepository eventRepository) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.eventRepository = eventRepository;
    }

    @Override
    public CategoryFullDto addCategory(CategoryPostDto categoryDto) {
        if (checkName(categoryDto)) {
            throw new ConflictException("Сategory exists!");
        }
        Category category = new Category();
        category.setName(categoryDto.getName());
        CategoryFullDto savedCategory = categoryMapper.toCategoryPostFullDto(categoryRepository.save(category));
        return savedCategory;
    }

    @Override
    public CategoryFullDto updateCategory(CategoryPostDto categoryDto, Integer id) {
        Category category = categoryRepository.findById(id).orElseThrow(()-> new NotFoundException(String.format("Category not found id - %s",id)));
        if (!category.getId().equals(id) && !category.getName().equals(categoryDto.getName())) {
            if (checkName(categoryDto)) {
                throw new ConflictException("Сategory exists!");
            }
        }

        category.setName(categoryDto.getName());
        CategoryFullDto savedCategory = categoryMapper.toCategoryPostFullDto(categoryRepository.save(category));
        return savedCategory;
    }

    @Override
    public CategoryFullDto getCategoryById(Integer id) {
       Optional<Category> category = Optional.ofNullable(categoryRepository.getCategoriesById(id));
        if (category.isEmpty())
        {
            throw new NotFoundException(String.format("Category not found id - %s",id));
        }
        return categoryMapper.toCategoryPostFullDto(category.get());
    }

    @Override
    public List<CategoryFullDto> getAllCategories(Integer from, Integer size) {
        List<Category> categoryList = categoryRepository.findAll().stream().skip(from).limit(size).toList();
        return categoryList.stream().map(categoryMapper::toCategoryPostFullDto).collect(Collectors.toList());
    }

    @Override
    public void deleteCategoryById(Integer id) {
        Category category = categoryRepository.findById(id).orElseThrow(()-> new NotFoundException(String.format("Category not found id - %s",id)));

        boolean check = eventRepository.findAll().stream()
                .map(Event::getCategory)
                .map(Category::getId)
                .anyMatch(ids -> id == ids);
        if (check) {
            throw new ConflictException("Category which have event");
        }

        categoryRepository.deleteById(id);
    }

    private boolean checkName(CategoryPostDto categoryDtoIn) {
        return categoryRepository.findAll().stream()
                .map(Category::getName)
                .anyMatch(name -> name.equals(categoryDtoIn.getName()));
    }
}
