package me.axelfrache.questify.service;

import lombok.RequiredArgsConstructor;
import me.axelfrache.questify.dto.UserDto;
import me.axelfrache.questify.dto.request.UpdatePasswordRequest;
import me.axelfrache.questify.dto.request.UpdateProfileRequest;
import me.axelfrache.questify.exception.BadRequestException;
import me.axelfrache.questify.exception.ResourceNotFoundException;
import me.axelfrache.questify.model.User;
import me.axelfrache.questify.repository.GradeRepository;
import me.axelfrache.questify.repository.TaskRepository;
import me.axelfrache.questify.repository.UserRepository;
import me.axelfrache.questify.util.ImageUtils;
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

    @Transactional
    public UserDto updateProfile(UUID userId, UpdateProfileRequest request) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        
        // Check if username is already taken
        if (!user.getUserName().equals(request.getUserName()) && 
                userRepository.existsByUserName(request.getUserName())) {
            throw new BadRequestException("Username is already taken");
        }
        
        // Check if email is already taken
        if (!user.getEmail().equals(request.getEmail()) && 
                userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already taken");
        }
        
        user.setUserName(request.getUserName());
        user.setEmail(request.getEmail());
        
        // Process profile picture if provided
        if (request.getProfilePicture() != null) {
            if (!ImageUtils.isValidBase64Image(request.getProfilePicture())) {
                throw new BadRequestException("Invalid image format. Please provide a valid Base64 encoded image");
            }
            user.setProfilePicture(ImageUtils.base64ToBytes(request.getProfilePicture()));
        }
        
        return mapToDto(userRepository.save(user));
    }
    
    @Transactional
    public UserDto updatePassword(UUID userId, UpdatePasswordRequest request) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        
        // Check if current password is correct
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }
        
        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        
        return mapToDto(userRepository.save(user));
    }
    
    private UserDto mapToDto(User user) {
        var completedTasks = taskRepository.countByUserAndCategoryAndCompleted(user, null, true);
        
        return UserDto.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .level(user.getLevel())
                .experience(user.getExperience())
                .gradeName(user.getCurrentGrade() != null ? user.getCurrentGrade().getName() : "Novice")
                .completedTasks((int) completedTasks)
                .achievements(user.getAchievements().size())
                .profilePicture(user.getProfilePicture() != null ? ImageUtils.bytesToBase64(user.getProfilePicture()) : null)
                .build();
    }
}
