package me.axelfrache.questify.repository;

import me.axelfrache.questify.model.Category;
import me.axelfrache.questify.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findByPredefined(boolean predefined);
    List<Category> findByUser(User user);
    List<Category> findByUserOrPredefined(User user, boolean predefined);
    Optional<Category> findByNameAndUser(String name, User user);
    Optional<Category> findByNameAndPredefined(String name, boolean predefined);
}
