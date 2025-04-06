package me.axelfrache.questify.service;

import lombok.RequiredArgsConstructor;
import me.axelfrache.questify.dto.UserDto;
import me.axelfrache.questify.exception.ResourceNotFoundException;
import me.axelfrache.questify.model.User;
import me.axelfrache.questify.repository.GradeRepository;
import me.axelfrache.questify.repository.TaskRepository;
import me.axelfrache.questify.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final GradeRepository gradeRepository;
    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserDto getCurrentUser(UUID userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        
        return mapToDto(user);
    }

    @Transactional
    public void updateUserGrade(User user) {
        var appropriateGrade = gradeRepository.findByMinLevelLessThanEqualAndMaxLevelGreaterThanEqual(
                user.getLevel(), user.getLevel())
                .orElse(null);
        
        if (appropriateGrade != null && 
                (user.getCurrentGrade() == null || !user.getCurrentGrade().getId().equals(appropriateGrade.getId()))) {
            user.setCurrentGrade(appropriateGrade);
            userRepository.save(user);
        }
    }

    @Transactional
    public UserDto addExperience(UUID userId, int experience) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        
        user.addExperience(experience);
        updateUserGrade(user);
        
        return mapToDto(userRepository.save(user));
    }

    private UserDto mapToDto(User user) {
        var completedTasks = taskRepository.countByUserAndCategoryAndCompleted(user, null, true);
        
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .level(user.getLevel())
                .experience(user.getExperience())
                .gradeName(user.getCurrentGrade() != null ? user.getCurrentGrade().getName() : "Novice")
                .completedTasks((int) completedTasks)
                .achievements(user.getAchievements().size())
                .build();
    }
}
