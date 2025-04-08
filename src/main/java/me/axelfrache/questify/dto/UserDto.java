package me.axelfrache.questify.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private UUID id;
    private String userName;
    private String email;
    private int level;
    private int experience;
    private String gradeName;
    private int completedTasks;
    private int achievements;
    private String profilePicture; // Base64 encoded image
}
