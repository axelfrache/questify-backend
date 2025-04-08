package me.axelfrache.questify.service;

import lombok.RequiredArgsConstructor;
import me.axelfrache.questify.dto.AchievementDto;
import me.axelfrache.questify.exception.ResourceNotFoundException;
import me.axelfrache.questify.model.Achievement;
import me.axelfrache.questify.model.Category;
import me.axelfrache.questify.model.User;
import me.axelfrache.questify.repository.AchievementRepository;
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
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TaskRepository taskRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public List<AchievementDto> getAllAchievements(UUID userId) {
        var user = getUserById(userId);
        
        var allAchievements = achievementRepository.findAll();
        
        return allAchievements.stream()
                .map(achievement -> mapToDto(achievement, user))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AchievementDto> getUnlockedAchievements(UUID userId) {
        var user = getUserById(userId);
        
        return achievementRepository.findByUsers(user).stream()
                .map(achievement -> mapToDto(achievement, user))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AchievementDto> getLockedAchievements(UUID userId) {
        var user = getUserById(userId);
        
        return achievementRepository.findByUsersNotContaining(user).stream()
                .map(achievement -> mapToDto(achievement, user))
                .collect(Collectors.toList());
    }

    @Transactional
    public void checkAndUnlockAchievements(UUID userId, UUID categoryId) {
        var user = getUserById(userId);
        var category = getCategoryById(categoryId);
        
        var categoryAchievements = achievementRepository.findByCategory(category);
        
        var completedTasksCount = taskRepository.countByUserAndCategoryAndCompleted(user, category, true);
        
        for (Achievement achievement : categoryAchievements) {
            if (!user.getAchievements().contains(achievement))
                if (completedTasksCount >= achievement.getRequiredCount()) {
                    user.getAchievements().add(achievement);
                    userService.addExperience(userId, achievement.getExperienceReward());
                }
        }
        
        userRepository.save(user);
    }

    @Transactional
    public void initializeAchievements() {
        if (!achievementRepository.findAll().isEmpty())
            return;
        
        var predefinedCategories = categoryRepository.findByPredefined(true);
        
        for (Category category : predefinedCategories) {
            createCategoryAchievements(category);
        }
    }

    private void createCategoryAchievements(Category category) {
        var achievements = List.of(
            Achievement.builder()
                .name("Beginner in " + category.getName())
                .description("Complete 5 tasks in the " + category.getName() + " category")
                .requiredCount(5)
                .experienceReward(50)
                .category(category)
                .build(),
            Achievement.builder()
                .name("Intermediate in " + category.getName())
                .description("Complete 15 tasks in the " + category.getName() + " category")
                .requiredCount(15)
                .experienceReward(150)
                .category(category)
                .build(),
            Achievement.builder()
                .name("Expert in " + category.getName())
                .description("Complete 30 tasks in the " + category.getName() + " category")
                .requiredCount(30)
                .experienceReward(300)
                .category(category)
                .build(),
            Achievement.builder()
                .name("Master in " + category.getName())
                .description("Complete 50 tasks in the " + category.getName() + " category")
                .requiredCount(50)
                .experienceReward(500)
                .category(category)
                .build()
        );
        
        achievementRepository.saveAll(achievements);
    }

    private User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
    }

    private Category getCategoryById(UUID categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + categoryId));
    }

    private AchievementDto mapToDto(Achievement achievement, User user) {
        var unlocked = user.getAchievements().contains(achievement);
        
        var currentProgress = 0;
        if (achievement.getCategory() != null)
            currentProgress = (int) taskRepository.countByUserAndCategoryAndCompleted(
                    user, achievement.getCategory(), true);
        
        return AchievementDto.builder()
                .id(achievement.getId())
                .name(achievement.getName())
                .description(achievement.getDescription())
                .requiredCount(achievement.getRequiredCount())
                .experienceReward(achievement.getExperienceReward())
                .categoryId(achievement.getCategory() != null ? achievement.getCategory().getId() : null)
                .categoryName(achievement.getCategory() != null ? achievement.getCategory().getName() : null)
                .unlocked(unlocked)
                .currentProgress(currentProgress)
                .build();
    }
}
