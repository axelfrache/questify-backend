package me.axelfrache.questify.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.axelfrache.questify.service.AchievementService;
import me.axelfrache.questify.service.CategoryService;
import me.axelfrache.questify.service.GradeService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final GradeService gradeService;
    private final CategoryService categoryService;
    private final AchievementService achievementService;

    @Override
    public void run(String... args) {
        log.info("Initialisation des données prédéfinies...");
        
        // Initialiser les grades
        gradeService.initializeGrades();
        log.info("Grades initialisés");
        
        // Initialiser les catégories prédéfinies
        categoryService.initializePredefinedCategories();
        log.info("Catégories prédéfinies initialisées");
        
        // Initialiser les achievements
        achievementService.initializeAchievements();
        log.info("Achievements initialisés");
        
        log.info("Initialisation des données terminée");
    }
}
