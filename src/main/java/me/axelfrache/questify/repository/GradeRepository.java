package me.axelfrache.questify.repository;

import me.axelfrache.questify.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GradeRepository extends JpaRepository<Grade, UUID> {
    Optional<Grade> findByName(String name);
    Optional<Grade> findByMinLevelLessThanEqualAndMaxLevelGreaterThanEqual(int level, int sameLevel);
}
