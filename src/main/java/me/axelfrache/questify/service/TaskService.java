package me.axelfrache.questify.service;

import lombok.RequiredArgsConstructor;
import me.axelfrache.questify.dto.TaskDto;
import me.axelfrache.questify.dto.request.TaskRequest;
import me.axelfrache.questify.exception.BadRequestException;
import me.axelfrache.questify.exception.ResourceNotFoundException;
import me.axelfrache.questify.model.Category;
import me.axelfrache.questify.model.Task;
import me.axelfrache.questify.model.User;
import me.axelfrache.questify.repository.CategoryRepository;
import me.axelfrache.questify.repository.TaskRepository;
import me.axelfrache.questify.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final UserService userService;
    private final AchievementService achievementService;

    @Transactional(readOnly = true)
    public List<TaskDto> getAllTasks(UUID userId) {
        var user = getUserById(userId);
        return taskRepository.findByUser(user).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskDto> getTasksByCategory(UUID userId, UUID categoryId) {
        var user = getUserById(userId);
        var category = getCategoryById(categoryId);
        return taskRepository.findByUserAndCategory(user, category).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskDto> getTasksByDifficulty(UUID userId, Task.TaskDifficulty difficulty) {
        var user = getUserById(userId);
        return taskRepository.findByUserAndDifficulty(user, difficulty).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskDto> getTasksByCompletion(UUID userId, boolean completed) {
        var user = getUserById(userId);
        return taskRepository.findByUserAndCompleted(user, completed).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TaskDto getTaskById(UUID userId, UUID taskId) {
        var task = getTaskByIdAndUserId(taskId, userId);
        return mapToDto(task);
    }

    @Transactional
    public TaskDto createTask(UUID userId, TaskRequest request) {
        var user = getUserById(userId);
        
        Category category = null;
        if (request.getCategoryId() != null) {
            category = getCategoryById(request.getCategoryId());
            
            if (!category.isPredefined() && (category.getUser() == null || !category.getUser().getId().equals(userId)))
                throw new BadRequestException("You are not authorized to use this category");
        }
        
        var task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .difficulty(request.getDifficulty())
                .completed(false)
                .createdAt(LocalDateTime.now())
                .dueDate(request.getDueDate())
                .user(user)
                .category(category)
                .build();
        
        return mapToDto(taskRepository.save(task));
    }

    @Transactional
    public TaskDto updateTask(UUID userId, UUID taskId, TaskRequest request) {
        var task = getTaskByIdAndUserId(taskId, userId);
        
        Category category = null;
        if (request.getCategoryId() != null) {
            category = getCategoryById(request.getCategoryId());
            
            if (!category.isPredefined() && (category.getUser() == null || !category.getUser().getId().equals(userId)))
                throw new BadRequestException("You are not authorized to use this category");
        }
        
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDifficulty(request.getDifficulty());
        task.setDueDate(request.getDueDate());
        task.setCategory(category);
        
        return mapToDto(taskRepository.save(task));
    }

    @Transactional
    public TaskDto completeTask(UUID userId, UUID taskId) {
        var task = getTaskByIdAndUserId(taskId, userId);
        
        if (task.isCompleted())
            throw new BadRequestException("This task is already completed");
        
        task.complete();
        var savedTask = taskRepository.save(task);

        userService.addExperience(userId, task.calculateXp());
        
        if (task.getCategory() != null)
            achievementService.checkAndUnlockAchievements(userId, task.getCategory().getId());
        
        return mapToDto(savedTask);
    }

    @Transactional
    public void deleteTask(UUID userId, UUID taskId) {
        var task = getTaskByIdAndUserId(taskId, userId);
        taskRepository.delete(task);
    }

    private User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
    }

    private Category getCategoryById(UUID categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + categoryId));
    }

    private Task getTaskByIdAndUserId(UUID taskId, UUID userId) {
        var task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));
        
        if (!task.getUser().getId().equals(userId))
            throw new BadRequestException("You are not authorized to access this task");
        
        return task;
    }

    private TaskDto mapToDto(Task task) {
        return TaskDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .difficulty(task.getDifficulty())
                .completed(task.isCompleted())
                .createdAt(task.getCreatedAt())
                .completedAt(task.getCompletedAt())
                .dueDate(task.getDueDate())
                .categoryId(task.getCategory() != null ? task.getCategory().getId() : null)
                .categoryName(task.getCategory() != null ? task.getCategory().getName() : null)
                .xpReward(task.calculateXp())
                .build();
    }
}
