package me.axelfrache.questify.repository;

import me.axelfrache.questify.model.Achievement;
import me.axelfrache.questify.model.Category;
import me.axelfrache.questify.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, UUID> {
    List<Achievement> findByCategory(Category category);
    List<Achievement> findByUsers(User user);
    List<Achievement> findByUsersNotContaining(User user);
}
