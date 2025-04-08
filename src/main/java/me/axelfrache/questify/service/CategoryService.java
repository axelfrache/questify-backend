package me.axelfrache.questify.service;

import lombok.RequiredArgsConstructor;
import me.axelfrache.questify.dto.CategoryDto;
import me.axelfrache.questify.dto.request.CategoryRequest;
import me.axelfrache.questify.exception.BadRequestException;
import me.axelfrache.questify.exception.ResourceNotFoundException;
import me.axelfrache.questify.model.Category;
import me.axelfrache.questify.model.User;
import me.axelfrache.questify.repository.CategoryRepository;
import me.axelfrache.questify.repository.TaskRepository;
import me.axelfrache.questify.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories(UUID userId) {
        var user = getUserById(userId);
        return categoryRepository.findByUserOrPredefined(user, true).stream()
                .map(category -> mapToDto(category, user))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(UUID userId, UUID categoryId) {
        var user = getUserById(userId);
        return mapToDto(getCategoryByIdAndValidateAccess(categoryId, user), user);
    }

    @Transactional
    public CategoryDto createCategory(UUID userId, CategoryRequest request) {
        var user = getUserById(userId);
        
        if (categoryRepository.findByNameAndUser(request.getName(), user).isPresent())
            throw new BadRequestException("A category with this name already exists");
        
        var category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .predefined(false)
                .user(user)
                .build();
        
        return mapToDto(categoryRepository.save(category), user);
    }

    @Transactional
    public CategoryDto updateCategory(UUID userId, UUID categoryId, CategoryRequest request) {
        var user = getUserById(userId);
        var category = getCategoryByIdAndValidateAccess(categoryId, user);
        
        if (category.isPredefined())
            throw new BadRequestException("You cannot modify a predefined category");
        
        categoryRepository.findByNameAndUser(request.getName(), user)
                .ifPresent(existingCategory -> {
                    if (!existingCategory.getId().equals(categoryId)) {
                        throw new BadRequestException("A category with this name already exists");
                    }
                });
        
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        
        return mapToDto(categoryRepository.save(category), user);
    }

    @Transactional
    public void deleteCategory(UUID userId, UUID categoryId) {
        var user = getUserById(userId);
        var category = getCategoryByIdAndValidateAccess(categoryId, user);
        
        if (category.isPredefined())
            throw new BadRequestException("You cannot delete a predefined category");
        
        if (!category.getTasks().isEmpty())
            throw new BadRequestException("You cannot delete a category that contains tasks");
        
        categoryRepository.delete(category);
    }

    @Transactional
    public void initializePredefinedCategories() {
        if (!categoryRepository.findByPredefined(true).isEmpty())
            return;
        
        var predefinedCategories = List.of(
            Category.builder().name("Sports").description("Physical activities and exercises").predefined(true).build(),
            Category.builder().name("Nutrition").description("Food and hydration").predefined(true).build(),
            Category.builder().name("Work").description("Professional tasks").predefined(true).build(),
            Category.builder().name("School").description("Homework and studies").predefined(true).build(),
            Category.builder().name("Home").description("Household chores and organization").predefined(true).build(),
            Category.builder().name("Leisure").description("Recreational activities and relaxation").predefined(true).build(),
            Category.builder().name("Personal Development").description("Self-improvement").predefined(true).build()
        );
        
        categoryRepository.saveAll(predefinedCategories);
    }

    private User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
    }

    private Category getCategoryByIdAndValidateAccess(UUID categoryId, User user) {
        var category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + categoryId));
        
        if (!category.isPredefined() && !category.getUser().getId().equals(user.getId()))
            throw new BadRequestException("You are not authorized to access this category");
        
        return category;
    }

    private CategoryDto mapToDto(Category category, User user) {
        var taskCount = taskRepository.countByCategoryIdAndUserId(category.getId(), user.getId());
        
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .predefined(category.isPredefined())
                .taskCount(taskCount)
                .build();
    }
}
