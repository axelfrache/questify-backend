package me.axelfrache.questify.repository;

import me.axelfrache.questify.model.Category;
import me.axelfrache.questify.model.Task;
import me.axelfrache.questify.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findByUser(User user);
    List<Task> findByUserAndCompleted(User user, boolean completed);
    List<Task> findByUserAndCategory(User user, Category category);
    List<Task> findByUserAndCategoryAndCompleted(User user, Category category, boolean completed);
    List<Task> findByUserAndDifficulty(User user, Task.TaskDifficulty difficulty);
    long countByUserAndCategoryAndCompleted(User user, Category category, boolean completed);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.category.id = :categoryId AND t.user.id = :userId")
    int countByCategoryIdAndUserId(@Param("categoryId") UUID categoryId, @Param("userId") UUID userId);
}
