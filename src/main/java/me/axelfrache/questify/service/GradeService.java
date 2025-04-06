package me.axelfrache.questify.service;

import lombok.RequiredArgsConstructor;
import me.axelfrache.questify.dto.GradeDto;
import me.axelfrache.questify.model.Grade;
import me.axelfrache.questify.repository.GradeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GradeService {

    private final GradeRepository gradeRepository;

    @Transactional(readOnly = true)
    public List<GradeDto> getAllGrades() {
        return gradeRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void initializeGrades() {
        if (!gradeRepository.findAll().isEmpty())
            return;
        
        var grades = List.of(
            Grade.builder()
                .name("Seeker")
                .description("You begin your quest towards accomplishment")
                .minLevel(1)
                .maxLevel(9)
                .build(),
            Grade.builder()
                .name("Apprentice")
                .description("You have mastered the basics of personal discipline")
                .minLevel(10)
                .maxLevel(19)
                .build(),
            Grade.builder()
                .name("Wanderer")
                .description("You are exploring new habits and routines")
                .minLevel(20)
                .maxLevel(29)
                .build(),
            Grade.builder()
                .name("Pathfinder")
                .description("You are charting your own path to success")
                .minLevel(30)
                .maxLevel(39)
                .build(),
            Grade.builder()
                .name("Strategist")
                .description("You master the art of planning and execution")
                .minLevel(40)
                .maxLevel(49)
                .build(),
            Grade.builder()
                .name("Guide")
                .description("Your example inspires others")
                .minLevel(50)
                .maxLevel(69)
                .build(),
            Grade.builder()
                .name("Questmaster")
                .description("You are a master in the art of achieving your goals")
                .minLevel(70)
                .maxLevel(100)
                .build()
        );
        
        gradeRepository.saveAll(grades);
    }

    private GradeDto mapToDto(Grade grade) {
        return GradeDto.builder()
                .id(grade.getId())
                .name(grade.getName())
                .description(grade.getDescription())
                .minLevel(grade.getMinLevel())
                .maxLevel(grade.getMaxLevel())
                .build();
    }
}
